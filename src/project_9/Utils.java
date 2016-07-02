package project_9;

import project_9.generator.Program;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Small utility class with some useful methods and configurable constants.
 *
 * Author:  Martijn
 * Date:    22-6-2016
 */
public class Utils {

    /** Base directory of sample programs */
    public static final String BASE_DIR = "src/project_9/samplePrograms/";

    /** Extension of Atlantis programs */
    public static final String EXT = ".atl";

    /** Base directory of generated Haskell programs  */
    public static final String BASE_HS_DIR = "src/project_9/haskellPrograms/";

    /** Extension of Haskell programs */
    private static final String HS_EXT = ".hs";

    /** String representation of the true value in SprIl */
    public static final String TRUE_VALUE = "1";

    /** STring representation of the false value in SprIl */
    public static final String FALSE_VALUE = "0";

    /** Size of a integer type in bytes. */
    public static final int INT_SIZE = 1;

    /** Size of a boolean type in bytes. */
    public static final int BOOL_SIZE = 1;

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

    /**
     * Prints text to console if debugging is on.
     * @param list List of Text to be printed.
     */
    public static void pr(List<String> list) {
        if (DEBUG) {
            for (String s : list) {
                pr(s);
            }
        }
    }

    /**
     * Creates a Haskell file of a given program with a given filename.
     * @param prog Program that is to be made into a Haskell file.
     * @param filename Name of the new Haskell file.
     */
    public static void toHaskellFile(Program prog, String filename) {
        try {
            List<String> code = prog.generateCode(filename);
            Path file = Paths.get(BASE_HS_DIR + filename + HS_EXT);
            Files.write(file, code, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
