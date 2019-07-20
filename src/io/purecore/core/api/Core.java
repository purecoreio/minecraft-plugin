package io.purecore.core.api;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;


public class Core {

    public static CoreConnection newConnection(InetSocketAddress ip, UUID minecraft_uuid, String minecraft_username, CoreKey key) throws IOException, ServerApiError {

        String postip = ip.getHostString();
        String postuuid = minecraft_uuid.toString();
        String postkey = key.getHash();

        URL url = new URL("https://api.purecore.io/rest/1/connection/new/");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ip", postip);
        params.put("uuid", postuuid);
        params.put("username", minecraft_username);
        params.put("key", postkey);

        // ------ QUERY

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(),"UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        // ----- RESULT

        String body = CharStreams.toString(new InputStreamReader(conn.getInputStream(), Charsets.UTF_8));

        JsonObject result = new JsonParser().parse(body).getAsJsonObject();

        if(result.has("error")){

            String msgidtotal = Long.toString(Instant.now().toEpochMilli());
            String msgid = msgidtotal.length() > 2 ? msgidtotal.substring(msgidtotal.length() - 2) : msgidtotal;

            throw new ServerApiError(result.get("error").getAsString());

        } else {

            JsonObject playerdata = result.get("player").getAsJsonObject();
            CorePlayer player = new CorePlayer(playerdata.get("username").getAsString(),UUID.fromString(playerdata.get("uuid").getAsString()),playerdata.get("verified").getAsBoolean());

            JsonObject statusdata = result.get("status").getAsJsonObject();

            String closedon = null;
            if(!statusdata.get("closedOn").isJsonNull()){
                closedon=statusdata.get("closedOn").getAsString();
            }

            CoreConnectionStatus status = new CoreConnectionStatus(statusdata.get("closed").getAsBoolean(),closedon,statusdata.get("openedOn").getAsString());

            JsonObject locationdata = result.get("location").getAsJsonObject();
            CoreLocation location = new CoreLocation(locationdata.get("country").getAsString(),locationdata.get("region").getAsString(),locationdata.get("city").getAsString());

            JsonObject instancedata = result.get("instance").getAsJsonObject();
            String instancetypestring = instancedata.get("type").getAsString();

            CoreInstance.InstanceType instancetype;

            if(instancetypestring == "NTW"){

                instancetype = CoreInstance.InstanceType.NTW;

            } else if(instancetypestring == "SVR"){

                instancetype = CoreInstance.InstanceType.SVR;

            } else {

                instancetype = CoreInstance.InstanceType.DEV;

            }

            CoreInstance instance = new CoreInstance(instancetype,instancedata.get("uuid").getAsString(),instancedata.get("name").getAsString());

            CoreConnection connection = new CoreConnection(result.get("uuid").getAsString(),location,status,player,instance);
            return connection;

        }

    }

}
