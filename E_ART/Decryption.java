package E_ART;

import utilities.BinaryTree;
import utilities.Key;
import utilities.Utils;

import java.util.Random;

public class Decryption {
    private static final int OFFSET_CONSTANT = 32;
    private static final int DELTA = 32;

    private static long variableOffset(long Nl, long Nr) {
        return Nl < BinaryTree.R ? (BinaryTree.R * Nl) % Nr : (BinaryTree.R * Nr) % Nl;
    }

    private static long prngGenerator(int seed) {
        Random prng = new Random(seed);
        return Math.abs(prng.nextLong());
    }

    public static String decrypt(Key key, String cipherText) {
        long N = key.N, variance = key.variance;
        StringBuilder plainText = new StringBuilder();

        int Nl = (int) (N % BinaryTree.LEN_MAX);
        int Nr = (int) BinaryTree.reflection(Nl);
        int varOffset = (int) variableOffset(Nl, Nr);

        for (int pos = 0; pos < cipherText.length(); pos++) {
            long prn = prngGenerator(pos);
            int dynOffset = (int) (prn % variance);

            int valOrg = cipherText.charAt(pos);
            int X = valOrg - dynOffset;

            int quotient;
            if ((X - (OFFSET_CONSTANT + varOffset)) <= 0) quotient = 1;
            else quotient = 0;

            X = (int) (BinaryTree.LEN_MAX * quotient + X) - OFFSET_CONSTANT - varOffset;
            int org_value = (int) BinaryTree.reflection(X);
            //if (org_value == 44) org_value = org_value + 14;
            if (org_value >= 0 && org_value <= 32 && org_value != 10) org_value = org_value + DELTA;
            plainText.append((char) (org_value));
        }

        return Utils.refine(plainText);
    }

}
