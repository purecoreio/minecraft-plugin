package io.purecore.core.console.utils;

public class Msgs {

    public void showWarning(String classerror, String classmsg){
        System.out.println("[CORE] ["+classerror+"] -> "+classmsg); // show general warning, debug info
    }

    public void showError(String classerror, String classmsg){
        System.out.println("[CORE] [!!!] ["+classerror+"] -> "+classmsg); // show severe errors or important info
    }

}
