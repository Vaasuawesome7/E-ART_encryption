package network;

import E_ART.Decryption;
import utilities.Key;
import utilities.Utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    private FileOutputStream outFile = null;
    private DataInputStream inData = null;
    private DataOutputStream outData = null;
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;

    private void serverConnection(int port) throws IOException {
        System.out.println("Server connecting");
        serverSocket = new ServerSocket(port);
        File cipherFile = new File(Utils.SERVER_FILES_LOCATION + "cipherText.txt");
        outFile = new FileOutputStream(cipherFile);
        System.out.println("Server connected...");
    }

    private void clientConnection() throws IOException {
        System.out.println("Waiting for client...");
        clientSocket = serverSocket.accept();
        inData = new DataInputStream(clientSocket.getInputStream());
        outData = new DataOutputStream(clientSocket.getOutputStream());
        System.out.println("Client connected...");
    }

    private void close() throws IOException {
        inData.close();
        outFile.close();
        outData.close();
        clientSocket.close();
        serverSocket.close();
    }

    private Key readKey() throws IOException {
        long N = inData.readLong();
        long variance = inData.readLong();
        return new Key((int) N, (int) variance);
    }

    Server(int port) throws IOException {
        serverConnection(port);
        clientConnection();

        // receiving key
        System.out.println("Receiving key...");
        Key key = readKey();
        System.out.println("Received key...");

        // receiving cipher text
        StringBuilder cipherTextBuilder = new StringBuilder();
        System.out.println("Receiving data from client...");
        long iterClient = inData.readLong();
        int i = 0;
        while (i < iterClient) {
            cipherTextBuilder.append(inData.readUTF());
            i++;
        }

        String cipherText = cipherTextBuilder.toString();
        outFile.write(cipherText.getBytes(StandardCharsets.UTF_8));
        System.out.println("Saved data into a new file...");

        // decrypting data
        System.out.println("Decrypting the data...");
        String plainTextDec = Decryption.decrypt(key, cipherText);

        // sending encrypted data
        System.out.println("Sending decrypted data to client...");
        int iter = (plainTextDec.length() / Utils.CHUNK_SIZE) + 1;
        outData.writeLong(iter);
        i = 0;
        while (i < iter) {
            outData.writeUTF(plainTextDec.substring(i*Utils.CHUNK_SIZE, Math.min((i+1)*Utils.CHUNK_SIZE, plainTextDec.length())));
            i++;
        }
        System.out.println("Data sent...");

        // closing the server
        System.out.println("Closing server...");
        close();
    }

    public static void main(String[] args) {
        try {
            new Server(5000);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
