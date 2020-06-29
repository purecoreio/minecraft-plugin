package io.purecore.mcplugin.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    Logger logger;

    public Log(Logger logger){
        this.logger=logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public void showTitle(String subtitle){
        this.logger.log(Level.INFO, " _ __   _   _  _ __  ___   ___  ___   _ __  ___ ");
        this.logger.log(Level.INFO, "| '_ \\ | | | || '__|/ _ \\ / __|/ _ \\ | '__|/ _ \\");
        this.logger.log(Level.INFO, "| |_) || |_| || |  |  __/| (__| (_) || |  |  __/");
        this.logger.log(Level.INFO, "| .__/  \\__,_||_|   \\___| \\___|\\___/ |_|   \\___|");
        this.logger.log(Level.INFO, "|_|                                             ");
        this.logger.log(Level.INFO, "");
        this.logger.log(Level.INFO, "→ "+subtitle);
    }

    public void logInfo(String info){
        this.logger.log(Level.INFO,info);
    }

    public void logWarning(String warning){
        this.logger.log(Level.WARNING,warning);
    }

    public void logError(String error){
        this.logger.log(Level.SEVERE,error);
    }

}
