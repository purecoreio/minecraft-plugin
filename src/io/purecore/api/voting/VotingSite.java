package io.purecore.api.voting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.purecore.api.user.Owner;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.TimeZone;

public class VotingSite {

    public String uuid;
    public Owner supervisor;
    public ArrayList<Integer> resetTimes;
    public TimeZone timeZone;
    public String name;
    public URL url;
    public String technicalName;

    public VotingSite(String serviceName){
        this.uuid=null;
        this.supervisor=null;
        this.resetTimes=new ArrayList<Integer>();
        this.timeZone=null;
        this.name=serviceName;
        this.url=null;
        this.technicalName=serviceName.replace(".","_");
    }

    public VotingSite(String uuid, Owner supervisor, ArrayList<Integer> resetTimes, TimeZone timeZone, String name, URL url, String technicalName){
        this.uuid=uuid;
        this.supervisor=supervisor;
        this.resetTimes=resetTimes;
        this.timeZone=timeZone;
        this.name=name;
        this.url=url;
        this.technicalName=technicalName;
    }

    public VotingSite(JsonObject json){
        this.uuid=json.get("uuid").getAsString();
        this.supervisor=new Owner(json.get("supervisor").getAsJsonObject());
        JsonArray timeArrayJSON = json.get("resetTimes").getAsJsonArray();
        this.resetTimes = new ArrayList<Integer>();
        for (JsonElement element:timeArrayJSON) {
            this.resetTimes.add(element.getAsInt());
        }
        this.timeZone=TimeZone.getTimeZone(json.get("timezone").getAsString());
        this.name=json.get("name").getAsString();
        try {
            this.url= URI.create("https://"+json.get("url").getAsString()).toURL();
        } catch (MalformedURLException e) {
            this.url= null;
        }
        this.technicalName=json.get("technicalName").getAsString();
    }



}
