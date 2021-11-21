package network;

import E_ART.Encryption;
import utilities.Key;
import utilities.Utils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    private Socket socket = null;
    private FileOutputStream outFile = null;
    private BufferedReader inFile = null;
    private DataInputStream inData = null;
    private DataOutputStream outData = null;

    private void connect(int port) throws IOException {
        System.out.println("Client connecting...");
        socket = new Socket("127.0.0.1", port);
        inData = new DataInputStream(socket.getInputStream());
        outData = new DataOutputStream(socket.getOutputStream());
        File plainTextClient = new File(Utils.CLIENT_FILES_LOCATION + "1000.txt");
        File plainTextServer = new File(Utils.CLIENT_FILES_LOCATION + "op.txt");
        inFile = new BufferedReader(new FileReader(plainTextClient));
        outFile = new FileOutputStream(plainTextServer);
        System.out.println("Client connected...");
    }

    private void close() throws IOException {
        inFile.close();
        outFile.close();
        inData.close();
        outData.close();
        socket.close();
    }

    private void writeKey(Key key) throws IOException {
        outData.writeLong(key.N);
        outData.writeLong(key.variance);
    }

    Client(int port, Key key) throws IOException {
        connect(port);

        System.out.println("Sending key...");
        writeKey(key);
        System.out.println("Sent the key to server...");

        System.out.println("Reading the plaintext data from file...");
        StringBuilder plainTextBuilder = new StringBuilder();
        String str;
        while ((str = inFile.readLine()) != null) {
            plainTextBuilder.append(str);
            plainTextBuilder.append("\n");
        }
        String plainText = plainTextBuilder.toString();

        System.out.println("Encrypting data...");
        String cipherText = Encryption.encrypt(key, plainText);

        System.out.println("Sending data to server...");
        long iter = (cipherText.length() / Utils.CHUNK_SIZE) + 1;
        outData.writeLong(iter);
        int i = 0;
        while (i < iter) {
            outData.writeUTF(cipherText.substring(i * Utils.CHUNK_SIZE, Math.min((i + 1) * Utils.CHUNK_SIZE, cipherText.length())));
            i++;
        }

        System.out.println("Receiving decrypted data from server...");
        long iterServer = inData.readLong();
        StringBuilder plainTextSBuilder = new StringBuilder();
        i = 0;
        while (i < iterServer) {
            plainTextSBuilder.append(inData.readUTF());
            i++;
        }
        String plainTextServer = plainTextSBuilder.toString();
        System.out.println("Writing decrypted data...");
        outFile.write(plainTextServer.getBytes(StandardCharsets.UTF_8));

        System.out.println("Closing client");
        close();
    }

    public static void main(String[] args) {
        try {
            Key key = new Key(32, 9);
            new Client(5000, key);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
