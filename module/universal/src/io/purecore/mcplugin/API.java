package io.purecore.mcplugin;

import io.purecore.api.Core;

public class API {

    protected static String currentRelease = "b8c0e54231910b24";
    public static String getCurrentRelease(){
        return  API.currentRelease;
    }
    public static Core instance;
    public static Core getInstance(){
        return API.instance;
    }

}
