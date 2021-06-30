package ght.app.datalogger.data.logSystem;

import java.util.Map;


/**
 * This Class is for printing message on the monitor. It will print it if the constant "printMode" is set to TRUE,
 * and if the certain reason (enun "Reason") "debugReasonMap" is set to true as well
 * @author M.Gasser
 * @version 1.000  12.05.2021
 */
public class PrintOnMonitor {
    //private static String pathToPackage = "DataLoggerApp/src/main/java/ght/app/files/";
    //private static File logfile = new File(pathToPackage + "monitorlog.txt");

    public enum Reason {
        GENERAL, GUI_ACTION, GUI_ALLERT, CONNECTION, CONNECTIONCHECK, THREAD, LISTENER, UNITINTERFACE, TESTSERVER, UNITTEST
    }

    //Config if printMode shall be active or not
    private static boolean printMode = true;
    private static boolean printModeInFile = false; //not yet programmed
    private static Reason lastReason = null;

    //Configuration what Printreasons shall get printed on the monitor (true=printmessages of this certain reason will get printed / false=printmessages of this certain reason doesn't get printed)
    private static Map<Reason, Boolean> debugReasonMap = Map.of(Reason.GENERAL,true,   Reason.GUI_ACTION,true,   Reason.GUI_ALLERT,false,   Reason.CONNECTION,true,   Reason.CONNECTIONCHECK,false,   Reason.THREAD,true,   Reason.LISTENER,true,   Reason.UNITINTERFACE,true, Reason.TESTSERVER,true, Reason.UNITTEST,true);



    /**
     * Methode will print on Monitor without terminate the line, if printMode is set and the one of the possible three reasons is set as well.
     * To still terminate the line just fill the parameter text with null or "".
     * The reason will also get printed if the lastReason is null or not equal to the one given into the methode.
     * Methode is overloaded with reason1 to 3
     * @param  text to be printed (as String)
     * @param reason1-3 reason to be checked if it shall get printed or not (as Reason)
     */
    public static void printMon(String text, Reason reason1) {
        if (printMode) {
            if (debugReasonMap.get(reason1)) {
                if (text != null && !text.equals("")) {
                    if (lastReason != null || lastReason == reason1) {
                        System.out.print(text);
                        lastReason = reason1;
                    } else {
                        //System.out.println();
                        System.out.print(reason1 + ": " + text);
                        lastReason = reason1;
                    }
                } else {
                    System.out.println();
                    lastReason = null;
                }
            }
        }
        if (printModeInFile) {
            logIntoFile(text);
        }
    }
    public static void printMon(String text, Reason reason1, Reason reason2) {
        if (printMode) {
            if (debugReasonMap.get(reason1)) {
                if (text != null && !text.equals("")) {
                    if (lastReason != null || lastReason == reason1) {
                        System.out.print(text);
                        lastReason = reason1;
                    } else {
                        System.out.print(reason1 + ": " + text);
                        lastReason = reason1;
                    }
                } else {
                    System.out.println();
                    lastReason = null;
                }
            } else if (debugReasonMap.get(reason2)) {
                if (text != null && !text.equals("")) {
                    if (lastReason != null || lastReason == reason2) {
                        System.out.print(text);
                        lastReason = reason2;
                    } else {
                        System.out.print(reason2 + ": " + text);
                        lastReason = reason2;
                    }
                } else {
                    System.out.println();
                    lastReason = null;
                }
            }
        }
        if (printModeInFile) {
            if (text != null && !text.equals("")) {
                logIntoFile(text);
            } else {
                loglnIntoFile("");
            }
        }
    }
    public static void printMon(String text, Reason reason1, Reason reason2, Reason reason3) {
        if (printMode) {
            if (debugReasonMap.get(reason1)) {
                if (text != null && !text.equals("")) {
                    if (lastReason != null || lastReason == reason1) {
                        System.out.print(text);
                        lastReason = reason1;
                    } else {
                        System.out.print(reason1 + ": " + text);
                        lastReason = reason1;
                    }
                } else {
                    System.out.println();
                    lastReason = null;
                }
            } else if (debugReasonMap.get(reason2)) {
                if (text != null && !text.equals("")) {
                    if (lastReason != null || lastReason == reason2) {
                        System.out.print(text);
                        lastReason = reason2;
                    } else {
                        System.out.print(reason2 + ": " + text);
                        lastReason = reason2;
                    }
                } else {
                    System.out.println();
                    lastReason = null;
                }
            } else if (debugReasonMap.get(reason3)) {
                if (text != null && !text.equals("")) {
                    if (lastReason != null || lastReason == reason3) {
                        System.out.print(text);
                        lastReason = reason3;
                    } else {
                        System.out.print(reason3 + ": " + text);
                        lastReason = reason3;
                    }
                } else {
                    System.out.println();
                    lastReason = null;
                }
            }
        }
        if (printModeInFile) {
            logIntoFile(text);
        }
    }

    /**
     * Methode will print on Monitor and then terminate the line, if printMode is set and the one of the possible three reasons is set as well.
     * Methode is overloaded with reason1 to 3
     * @param  text to be printed (as String)
     * @param reason1-3 reason to be checked if it shall get printed or not (as Reason)
     */
    public static void printlnMon(String text, Reason reason1) {
        if (printMode) {
            if (debugReasonMap.get(reason1)) {
                if (lastReason != null) {
                    System.out.println();
                }
                System.out.println(reason1.toString() + ": " + text);
                lastReason = null;
            }
        }
        if (printModeInFile) {
            loglnIntoFile(text);
        }
    }
    public static void printlnMon(String text, Reason reason1, Reason reason2) {
        if (debugReasonMap.get(reason1)) {
            if (lastReason != null) {
                System.out.println();
            }
            System.out.println(reason1 + ": " + text);
            lastReason = null;
        } else if (debugReasonMap.get(reason2)) {
            if (lastReason != null) {
                System.out.println();
            }
            System.out.println(reason2 + ": " + text);
            lastReason = null;
        }
        if (printModeInFile) {
            loglnIntoFile(text);
        }
    }
    public static void printlnMon(String text, Reason reason1, Reason reason2, Reason reason3) {
        if (printMode) {
            if (debugReasonMap.get(reason1)) {
                if (lastReason != null) {
                    System.out.println();
                }
                System.out.println(reason1 + ": " + text);
                lastReason = null;
            } else if (debugReasonMap.get(reason2)) {
                if (lastReason != null) {
                    System.out.println();
                }
                System.out.println(reason2 + ": " + text);
                lastReason = null;
            } else if (debugReasonMap.get(reason3)) {
                if (lastReason != null) {
                    System.out.println();
                }
                System.out.println(reason3 + ": " + text);
                lastReason = null;
            }
        }
        if (printModeInFile) {
            loglnIntoFile(text);
        }
    }

    /**
     * Methode will set/reset the debugmode as asked for.
     * @param  debugMode: true=everything (if reason is set) will be printed on the monitor / false=no print on Monitor  (as boolean)
     */
    public static void setDebugMode(boolean debugMode) {
        PrintOnMonitor.printMode = debugMode;
    }

    /**
     * Methode will log into file without terminate the line (not yet programmed)
     * @param  text (as String)
     */
    private static void logIntoFile(String text) {
        //not yet programmed
    }

    /**
     * Methode will log into file and terminate the line (not yet programmed)
     * Warning: text can be null or ""
     * @param  text (as String)
     */
    private static void loglnIntoFile(String text) {
        //not yet programmed
    }
}
