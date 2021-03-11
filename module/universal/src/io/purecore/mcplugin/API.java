package io.purecore.mcplugin;

import io.purecore.api.Core;

public class API {

    protected static String currentRelease = "5af58ea03c7f0c02";
    public static String getCurrentRelease(){
        return  API.currentRelease;
    }
    public static Core instance;
    public static Core getInstance(){
        return API.instance;
    }

}
