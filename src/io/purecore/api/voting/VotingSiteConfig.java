package io.purecore.api.voting;

import com.google.gson.JsonObject;
import io.purecore.api.Core;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.instance.Network;
import io.purecore.api.request.ObjectRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;

public class VotingSiteConfig {

    public Network network;
    public VotingSite votingSite;
    public URL url;

    public VotingSiteConfig(Network network, VotingSite votingSite, URL url){
        this.network=network;
        this.votingSite=votingSite;
        this.url=url;
    }

    public VotingSiteConfig(Core core, JsonObject json){
        this.network=new Network(json.get("network").getAsJsonObject(), core);
        this.votingSite=new VotingSite(json.get("votingSite").getAsJsonObject());
        try {
            this.url = URI.create(json.get("url").getAsString()).toURL();
        } catch (MalformedURLException e) {
            this.url = null;
        }
    }

    public VotingSiteConfig setURL(String url) throws IOException, ApiException, CallException {
        return this.setURL(URI.create(url).toURL());
    }

    public VotingSiteConfig setURL(URL url) throws ApiException, IOException, CallException {
        ObjectRequest request = new ObjectRequest(this.network.getCore(), ObjectRequest.Call.SETUP_VOTING_SITE);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("site",this.votingSite.uuid);
        params.put("url",url.toString());
        JsonObject result = request.getResult();
        this.network=new Network(result.get("network").getAsJsonObject(), this.network.getCore());
        this.votingSite=new VotingSite(result.get("votingSite").getAsJsonObject());
        try {
            this.url = URI.create(result.get("url").getAsString()).toURL();
        } catch (MalformedURLException e) {
            this.url = null;
        }
        return this;
    }


}
