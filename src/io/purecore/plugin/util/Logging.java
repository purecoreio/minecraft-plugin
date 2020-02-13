package io.purecore.plugin.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {

    public enum Class {
        STARTUP,
        SESSIONS,
        ADVANCEMENTS,
        STATISTICS,
        QUEUE,
        EXECUTIONS,
        COMMANDS
    }

    public static void logError(Logger logger, Class fromClass, String msg){
        logger.log(Level.WARNING, "["+fromClass.toString().toLowerCase()+"] "+msg);
    }

    public static void logInfo(Logger logger, Class fromClass, String msg){
        logger.log(Level.INFO, "["+fromClass.toString().toLowerCase()+"] "+msg);
    }

    public static void showBigError(Logger logger, String error){

        logger.log(Level.SEVERE, " _ __   _   _  _ __  ___   ___  ___   _ __  ___ ");
        logger.log(Level.SEVERE, "| '_ \\ | | | || '__|/ _ \\ / __|/ _ \\ | '__|/ _ \\");
        logger.log(Level.SEVERE, "| |_) || |_| || |  |  __/| (__| (_) || |  |  __/");
        logger.log(Level.SEVERE, "| .__/  \\__,_||_|   \\___| \\___|\\___/ |_|   \\___|");
        logger.log(Level.SEVERE, "|_|                                             ");
        logger.log(Level.SEVERE, "");
        logger.log(Level.SEVERE, "→ Error:");
        logger.log(Level.SEVERE, error);
        logger.log(Level.SEVERE, "");

    }

}
