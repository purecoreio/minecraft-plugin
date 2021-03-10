package io.purecore.mcplugin;

import io.purecore.api.Core;

public class API {

    protected static String currentRelease = "2c47a1bf59ab6ba4";
    public static String getCurrentRelease(){
        return  API.currentRelease;
    }
    public static Core instance;
    public static Core getInstance(){
        return API.instance;
    }

}
