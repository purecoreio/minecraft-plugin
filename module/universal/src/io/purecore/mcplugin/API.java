package io.purecore.mcplugin;

import io.purecore.api.Core;

public class API {

    protected static String currentRelease = "efa2588b328eb35d";
    public static String getCurrentRelease(){
        return  API.currentRelease;
    }
    public static Core instance;
    public static Core getInstance(){
        return API.instance;
    }

}
