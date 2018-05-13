package simcom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

public class AuxiliaryUtility {
    private static final String APPLICATION_NAME = "SimCom - Similarity Comparator";

    private static final String CATALOG_FILENAME = "catalog.bin";
    private static final String SUMMARY_HTML_FILENAME = "summary.html";
    private static final String SUMMARY_CSS_FILENAME = "summary.css";

    private static final String TMP_ROOT_DIRECTORY = System.getProperty("java.io.tmpdir");
    private static final String TMP_SIMCOM_DIRECTORY = "SimCom";
    private static final String IMAGES_DIRECTORY = TMP_ROOT_DIRECTORY + File.separator
                                                 + TMP_SIMCOM_DIRECTORY + File.separator + "images" + File.separator;
    private static final String STYLES_DIRECTORY = TMP_ROOT_DIRECTORY + File.separator
                                                 + TMP_SIMCOM_DIRECTORY + File.separator + "styles" + File.separator;

    private static final String HTML_BLANK_PAGE =
            "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"></head>" +
            "<body style=\"background-color: Gainsboro; font-family: Arial;\">" +
            "<p>Content will be added after graphs comparing.</p></body></html>";

    private static boolean debugMode;
    private static boolean resizableStage;
    private static String dotExecFilename;
    private static String dotExecPathname;
    private static String httpUserAgent;

    static String getApplicationName() {
        return APPLICATION_NAME;
    }

    static String getSummaryCssFilename() {
        return SUMMARY_CSS_FILENAME;
    }

    static String getSummaryHtmlPathname() {
        return TMP_ROOT_DIRECTORY + File.separator + TMP_SIMCOM_DIRECTORY + File.separator + SUMMARY_HTML_FILENAME;
    }

    static String getCatalogFilename() {
        return CATALOG_FILENAME;
    }

    static String getImagesDirectory() {
        return IMAGES_DIRECTORY;
    }

    static String getStylesDirectory() {
        return STYLES_DIRECTORY;
    }

    static boolean isDebugMode() {
        return debugMode;
    }

    private static void setDebugModeOn() {
        debugMode = true;
    }

    static boolean isResizableStage() {
        return resizableStage;
    }

    private static void setResizableStageOn() {
        resizableStage = true;
    }

    static String getDotExecPathname() {
        return dotExecPathname;
    }

    static String getHttpUserAgent() {
        return httpUserAgent;
    }

    static void setHttpUserAgent(String httpUserAgent) {
        AuxiliaryUtility.httpUserAgent = httpUserAgent;
    }

    private static void setDotExecPathname(String dotExecPathname) {
        AuxiliaryUtility.dotExecPathname = dotExecPathname;
    }

    static String getHtmlBlankPage() {
        return HTML_BLANK_PAGE;
    }

    static void parseCommandLineParameters(String[] args) {
        for (String s : args) {
            if (s.equals("debug")) {
                setDebugModeOn();
            }
            if (s.equals("resizable")) {
                setResizableStageOn();
            }
        }
    }

    private static String findExecutableOnPath(String filename) {
        for (String dirname : System.getenv("PATH").split(File.pathSeparator)) {
            File file = new File(dirname, filename);
            if (file.isFile() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    static boolean dotExecutableExists() {
        String dotExecPathname = AuxiliaryUtility.findExecutableOnPath(dotExecFilename);
        if (dotExecPathname != null) {
            AuxiliaryUtility.setDotExecPathname(dotExecPathname);
            return true;
        } else {
            return false;
        }
    }

    static boolean isOSTypeSupported() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            dotExecFilename = "dot.exe";
        }
        else if (os.contains("nix") || os.contains("aix") || os.contains("nux")){
            dotExecFilename = "dot";
        }
        else {
            return false;
        }
        return true;
    }

    static void deleteTemporaryDirectories() {
        Path path = Paths.get(TMP_ROOT_DIRECTORY).resolve(TMP_SIMCOM_DIRECTORY);
        if (Files.exists(path) && Files.isDirectory(path)) {
            try {
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                Dialogs.exceptionDialog(e);
            }
        }
    }

    static boolean makeTemporaryDirectories() {
        return new File(IMAGES_DIRECTORY).mkdirs() && new File(STYLES_DIRECTORY).mkdirs();
    }

    public static String ByteArrayAsBinLittleEndian (byte[] a) {
        StringBuilder rv = new StringBuilder();

        if (a.length > 0) {
            for (int i = a.length; i > 0; i--) {

                byte mask = 1;
                StringBuilder s = new StringBuilder();
                for (int j = 0; j < 8; j++) {
                    if ((a[i - 1] & mask) == 0) {
                        s.insert(0, '0');
                    } else {
                        s.insert(0, '1');
                    }
                    mask <<= 1;
                }

                rv.append(s.toString());

                if (i > 1) {
                    rv.append(' ');
                }

            }
            return rv.toString();
        } else {
            return "";
        }
    }

    public static String ByteArrayAsHexBigEndian (byte[] a) {
        StringBuilder rv = new StringBuilder();

        if (a.length > 0) {
            for (byte b : a) {
                rv.append(String.format("%02X", b));
            }
            return rv.toString();
        } else {
            return "";
        }
    }

    public static String ByteArrayAsHexLittleEndian (byte[] a) {
        StringBuilder rv = new StringBuilder();

        if (a.length > 0) {
            for (int i = a.length; i > 0; i--) {
                rv.append(String.format("%02X", a[i - 1]));
            }
            return rv.toString();
        } else {
            return "";
        }
    }

}
