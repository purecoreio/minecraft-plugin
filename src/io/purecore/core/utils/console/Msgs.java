package io.purecore.core.utils.console;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Msgs {

    public static void showWarning(Logger logger, String classerror, String classmsg){

        logger.log(Level.WARNING, "["+classerror.toLowerCase()+"] -> "+classmsg);
    }

    public static void showError(Logger logger, String classerror, String classmsg){
        logger.log(Level.SEVERE, "[!!!] ["+classerror.toLowerCase()+"] -> "+classmsg);
    }

}
