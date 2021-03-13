package io.purecore.mcplugin;

import io.purecore.api.Core;

public class API {

    protected static String currentRelease = "0752b18c37be4dc8";
    public static String getCurrentRelease(){
        return  API.currentRelease;
    }
    public static Core instance;
    public static Core getInstance(){
        return API.instance;
    }

}
