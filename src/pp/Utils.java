package pp;

/**
 * Small utility class with some useful methods.
 *
 * Author:  Martijn
 * Date:    22-6-2016
 */
public class Utils {

    /** Static boolean that determines debugging */
    public static final boolean DEBUG = false;

    /**
     * Prints text to console if debugging is on.
     * @param text Text to be printed.
     */
    public static void pr(String... text) {
        if (DEBUG) {
            for (String s : text) {
                System.out.println(s);
            }
        }
    }

}
