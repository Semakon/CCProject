package project_9;

/**
 * Small utility class with some useful methods and configurable constants.
 *
 * Author:  Martijn
 * Date:    22-6-2016
 */
public class Utils {

    /** Base directory of sample programs  */
    public static final String BASE_DIR = "src/project_9/samplePrograms/";

    /** Extension of Atlantis programs */
    public final static String EXT = ".atl";

    /** Size of a integer type in bytes. */
    public final static int INT_SIZE = 4;

    /** Size of a boolean type in bytes. */
    public final static int BOOL_SIZE = 4;

    /** Static boolean that determines debugging */
    public static final boolean DEBUG = true;

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
