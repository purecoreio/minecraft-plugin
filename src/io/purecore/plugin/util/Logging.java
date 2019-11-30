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

        logger.log(Level.INFO, " _ __   _   _  _ __  ___   ___  ___   _ __  ___ ");
        logger.log(Level.INFO, "| '_ \\ | | | || '__|/ _ \\ / __|/ _ \\ | '__|/ _ \\");
        logger.log(Level.INFO, "| |_) || |_| || |  |  __/| (__| (_) || |  |  __/");
        logger.log(Level.INFO, "| .__/  \\__,_||_|   \\___| \\___|\\___/ |_|   \\___|");
        logger.log(Level.INFO, "|_|                                             ");
        logger.log(Level.INFO, "");
        logger.log(Level.INFO, "→ Error:");
        logger.log(Level.INFO, error);
        logger.log(Level.INFO, "");

    }

}
