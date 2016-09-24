package filmeUtils;

public class Debug {
    public static final boolean IS_DEBUG = false;

    public static void log(final String message) {
        if (IS_DEBUG) {
            System.out.println("[DEBUG] " + message);
        }
    }
}
