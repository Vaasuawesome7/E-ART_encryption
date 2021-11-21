package metrics;

import E_ART.Decryption;
import E_ART.Encryption;
import utilities.Key;
import utilities.Utils;

import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

class DES {
    //creating an instance of the Cipher class for encryption
    private static Cipher encrypt;
    //creating an instance of the Cipher class for decryption
    private static Cipher decrypt;
    //initializing vector
    private static final byte[] initialization_vector = {22, 33, 11, 44, 55, 99, 66, 77};

    private static final String fileName = "1000.txt";

    //method for encryption
    private static void encryption(InputStream input, OutputStream output)
            throws IOException {
        output = new CipherOutputStream(output, encrypt);

        //calling the writeBytes() method to write the encrypted bytes to the file
        writeBytes(input, output);
    }

    //method for decryption
    private static void decryption(InputStream input, OutputStream output)
            throws IOException {
        input = new CipherInputStream(input, decrypt);

        //calling the writeBytes() method to write the decrypted bytes to the file
        writeBytes(input, output);
    }

    //method for writing bytes to the files
    private static void writeBytes(InputStream input, OutputStream output) throws IOException {
        byte[] writeBuffer = new byte[512];
        int readBytes;
        while ((readBytes = input.read(writeBuffer)) >= 0) {
            output.write(writeBuffer, 0, readBytes);
        }

        //closing the output stream
        output.close();

        //closing the input stream
        input.close();
    }

    private static void checkForEART() throws IOException {
        long startTime = System.currentTimeMillis();
        long initialMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        File plainTextClient = new File(Utils.CLIENT_FILES_LOCATION + fileName);
        BufferedReader inFile = new BufferedReader(new FileReader(plainTextClient));

        StringBuilder plainTextBuilder = new StringBuilder();
        String str;
        while ((str = inFile.readLine()) != null) {
            plainTextBuilder.append(str);
            plainTextBuilder.append("\n");
        }
        String plainText = plainTextBuilder.toString();
        String cipherText = Encryption.encrypt(new Key(32, 9), plainText);
        Decryption.decrypt(new Key(32, 9), cipherText);
        long finalMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long endTime = System.currentTimeMillis();

        long timeTaken = endTime - startTime;
        long memoryUsed = finalMem - initialMem;

        System.out.println("Time Taken for E-ART: " + timeTaken + " ms");
        System.out.println("Space consumed for E-ART: " + (double) memoryUsed / 1000000 + " MB");
    }

    //main() method
    public static void main(String[] args) {

        //path of the file that we want to encrypt
        String textFile = Utils.CLIENT_FILES_LOCATION + fileName;

        //path of the encrypted file that we get as output
        String encryptedData = Utils.DES + "DESEncrypt.txt";

        //path of the decrypted file that we get as output
        String decryptedData = Utils.DES + "DESDecrypt.txt";

        try {
            //generating keys by using the KeyGenerator class
            long startTime = System.currentTimeMillis();
            long initialMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            SecretKey scrtkey = KeyGenerator.getInstance("DES").generateKey();
            AlgorithmParameterSpec aps = new IvParameterSpec(initialization_vector);

            //setting encryption mode
            encrypt = Cipher.getInstance("DES/CBC/PKCS5Padding");
            encrypt.init(Cipher.ENCRYPT_MODE, scrtkey, aps);

            //setting decryption mode
            decrypt = Cipher.getInstance("DES/CBC/PKCS5Padding");
            decrypt.init(Cipher.DECRYPT_MODE, scrtkey, aps);
            long timeMidI = System.currentTimeMillis();
            //calling encrypt() method to encrypt the file
            encryption(new FileInputStream(textFile), new FileOutputStream(encryptedData));
            long timeMid = System.currentTimeMillis() - timeMidI;
            //calling decrypt() method to decrypt the file
            decryption(new FileInputStream(encryptedData), new FileOutputStream(decryptedData));
            long finalMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long endTime = System.currentTimeMillis() - timeMid;
            long timeTaken = endTime - startTime;

            long memoryUsed = finalMem - initialMem;

            System.out.println("Time Taken for DES: " + timeTaken + " ms");
            System.out.println("Space consumed for DES: " + (double) memoryUsed / 1000000 + " MB");
            //prints the statement if the program runs successfully
            //checkForEART();
        }

        //catching multiple exceptions by using the | (or) operator in a single catch block
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IOException e) {

            //prints the message (if any) related to exceptions
            e.printStackTrace();
        }
    }
}