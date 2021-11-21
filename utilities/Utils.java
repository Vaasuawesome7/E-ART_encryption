package utilities;

public class Utils {
    public static final int CHUNK_SIZE = 60000;
    public static final String CLIENT_FILES_LOCATION = "...src\\network\\client_files\\";
    public static final String SERVER_FILES_LOCATION = "...src\\network\\server_files\\";
    public static final String DES = "...src\\des_output_files\\";

    public static String refine(StringBuilder builder) {
        String res = builder.toString();
        res = res.replace("@", " ");
        res = res.replace("*", " ");
        return res;
    }
}
