package io.purecore.core.console.utils;

public class Title {

    public void showTitle(String subtitle){ // show the plugin Title in the console
        System.out.println(" ");
        System.out.println("   ___ ___  _ __ ___   ");
        System.out.println("  / __/ _ \\| '__/ _ \\  ");
        System.out.println(" | (_| (_) | | |  __/_ ");
        System.out.println("  \\___\\___/|_|  \\___(_)");
        System.out.println(" ");
        if(subtitle!=null){
            System.out.println(" -> " + subtitle);
        }
        System.out.println(" ");
    }

}
