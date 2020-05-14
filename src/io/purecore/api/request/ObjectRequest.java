package io.purecore.api.request;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.purecore.api.Core;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectRequest extends Core {

    private Core core;
    private LinkedHashMap<String, String> params;
    private final Object call;

    public enum Call {
        CONNECTION_GET,
        CONNECTION_CREATE,
        INSTANCE_GET,
        MARK_EXECUTION,
        PLAYER_FROM_USERNAME,
        CREATE_PUNISHMENT,
        REGEN_HASH,
        SETUP_VOTING_SITE,
        VOTE_WITH_SITE
    }

    public ObjectRequest(Core core, Call call){
        super(core.getKey());
        this.core = core;
        this.call = call;
        this.params = new LinkedHashMap<String, String>();
    }

    public ObjectRequest(Core core, Call call, LinkedHashMap<String, String> params){
        super(core.getKey());
        this.core = core;
        this.call = call;
        this.params = params;
    }

    private URL getURL() throws CallException, MalformedURLException {

        String base = "https://api.purecore.io/rest/2/";
        String url = null;
        if(this.call==Call.CONNECTION_CREATE){
            url="connection/new/";
        } else if(this.call==Call.CONNECTION_GET){
            url="connection/info/";
        } else if(this.call==Call.INSTANCE_GET){
            url="instance/info/";
        } else if(this.call==Call.MARK_EXECUTION){
            url="cmds/execute/";
        } else if(this.call==Call.PLAYER_FROM_USERNAME){
            url="player/from/minecraft/username/";
        } else if(this.call==Call.CREATE_PUNISHMENT){
            url="punishment/create/";
        } else if(this.call==Call.REGEN_HASH){
            url="key/regen/";
        } else if(this.call==Call.SETUP_VOTING_SITE){
            url="voting/site/setup/";
        } else if(this.call==Call.VOTE_WITH_SITE){
            url="instance/network/voting/with/site/";
        }

        if(url!=null){
            return new URL(base+url);
        } else {
            throw new CallException("invalid path generated, base and relative url can't be null!");
        }

    }

    private byte[] getParamBytes() throws UnsupportedEncodingException {

        // adds key to every call

        LinkedHashMap<String, String> params = this.params;
        this.params.put("key",this.core.getKey());

        // transforms call to bytes

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(),"UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        return postData.toString().getBytes(StandardCharsets.UTF_8);

    }

    private HttpURLConnection urlConnection(URL url, byte[] paramBytes) throws IOException {

        // starts connection

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(paramBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(paramBytes);
        return conn;

    }

    public JsonObject getResult() throws IOException, CallException, ApiException {

        HttpURLConnection conn = this.urlConnection(this.getURL(),this.getParamBytes());
        String body = CharStreams.toString(new InputStreamReader(conn.getInputStream(), Charsets.UTF_8));

        JsonElement result = new JsonParser().parse(body);
        if(result.isJsonObject()){
            JsonObject finalResult = result.getAsJsonObject();
            if(finalResult.has("error")){
                throw new ApiException(finalResult.get("error").getAsString());
            } else {
                return finalResult;
            }
        } else {
            throw new CallException("Received invalid type, expecting object, received array");
        }
    }

}
