package io.purecore.api.request;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.purecore.api.Core;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ArrayRequest extends Core {

    private Core core;
    private LinkedHashMap<String, String> params;
    private final Object call;

    public enum Call {
        CLOSE_ACTIVE_CONNECTIONS,
        UPDATE_PLAYER_ADVANCEMENTS,
        GET_PENDING_EXECUTIONS,
        GET_PENDING_EXECUTIONS_REDUCED,
        GET_OFFENCE_LIST
    }

    public ArrayRequest(Core core, Call call, LinkedHashMap<String, String> params){
        super(core.getKey());
        this.core=core;
        this.call = call;
        this.params = params;
    }

    public ArrayRequest(Core core, Call call){
        super(core.getKey());
        this.core=core;
        this.call = call;
        this.params = new LinkedHashMap<String, String>();
    }

    private URL getURL() throws CallException, MalformedURLException {

        String base = "https://api.purecore.io/rest/2/";
        String url = null;
        if(this.call== Call.CLOSE_ACTIVE_CONNECTIONS){
            url="connection/close/all/";
        } else if(this.call== Call.UPDATE_PLAYER_ADVANCEMENTS){
            url="advancement/list/add/";
        } else if(this.call== Call.GET_PENDING_EXECUTIONS){
            url="cmds/get/pending/";
        } else if(this.call== Call.GET_PENDING_EXECUTIONS_REDUCED){
            url="cmds/get/pending/reduced/";
        } else if(this.call== Call.GET_OFFENCE_LIST){
            url="punishment/offence/list/";
        }

        if(url!=null){
            return new URL(base+url);
        } else {
            throw new CallException("invalid path generated, base and relative url can't be null!");
        }

    }

    private byte[] getParamBytes() throws UnsupportedEncodingException {

        LinkedHashMap<String, String> params = this.params;
        params.put("key",core.getKey());

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(),"UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        return postData.toString().getBytes(StandardCharsets.UTF_8);

    }

    private String getParamString() throws UnsupportedEncodingException {
        return new String(getParamBytes());
    }

    private HttpURLConnection urlConnection(URL url, byte[] paramBytes) throws IOException {

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(paramBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(paramBytes);
        return conn;

    }

    public JsonArray getResult() throws IOException, CallException, ApiException {

        HttpURLConnection conn = this.urlConnection(this.getURL(),this.getParamBytes());
        String body = CharStreams.toString(new InputStreamReader(conn.getInputStream(), Charsets.UTF_8));

        JsonElement result = new JsonParser().parse(body);
        if(result.isJsonArray()){

            return result.getAsJsonArray();

        } else {

            JsonObject finalResult = result.getAsJsonObject();
            if(finalResult.has("error")){
                throw new ApiException(finalResult.get("error").getAsString());
            } else {
                throw new CallException("Received invalid type, expecting array, received object");
            }

        }
    }
}
