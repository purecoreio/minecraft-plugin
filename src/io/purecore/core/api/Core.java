package io.purecore.core.api;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.*;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

import static io.purecore.core.api.util.json.transform.*;


public class Core {

    public static boolean markExecution(CoreKey key, CoreExecution coreExecution) throws IOException, ServerApiError {

        String postkey = key.getHash();

        URL url = new URL("http://api.purecore.io/rest/1/cmds/execute/");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("key", postkey);
        params.put("execution", coreExecution.getUuid());

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

        if(new JsonParser().parse(body).isJsonObject()){
            JsonObject result = new JsonParser().parse(body).getAsJsonObject();
            if(result.has("error")){

                String msgidtotal = Long.toString(Instant.now().toEpochMilli());
                String msgid = msgidtotal.length() > 2 ? msgidtotal.substring(msgidtotal.length() - 2) : msgidtotal;

                throw new ServerApiError(result.get("error").getAsString());

            } else {
                if(result.has("success")){
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public static List<CoreExecution> getPendingExecutions(CoreKey key) throws IOException, ServerApiError {

        String postkey = key.getHash();

        URL url = new URL("http://api.purecore.io/rest/1/cmds/get/pending/");
        Map<String, Object> params = new LinkedHashMap<>();
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

        if(new JsonParser().parse(body).isJsonObject()){
            JsonObject result = new JsonParser().parse(body).getAsJsonObject();
            if(result.has("error")){

                String msgidtotal = Long.toString(Instant.now().toEpochMilli());
                String msgid = msgidtotal.length() > 2 ? msgidtotal.substring(msgidtotal.length() - 2) : msgidtotal;

                throw new ServerApiError(result.get("error").getAsString());

            } else {
                throw new ServerApiError("Unknown response");
            }
        } else {
            List<CoreExecution> commandList = new ArrayList<>();
            JsonArray result = new JsonParser().parse(body).getAsJsonArray();
            result.forEach((dt) ->
            {
                if (dt.isJsonObject())
                {
                    JsonObject executionJson = dt.getAsJsonObject();
                    commandList.add(executionFromJSON(executionJson));
                }
            });
            return commandList;
        }
    }

    public static List<CoreConnection> closePlayerConnections(UUID player_uuid, CoreKey key) throws IOException, ServerApiError {

        String postkey = key.getHash();

        URL url = new URL("https://api.purecore.io/rest/1/connection/close/all/");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("uuid", player_uuid.toString());
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

        if(new JsonParser().parse(body).isJsonObject()){
            JsonObject result = new JsonParser().parse(body).getAsJsonObject();
            if(result.has("error")){

                String msgidtotal = Long.toString(Instant.now().toEpochMilli());
                String msgid = msgidtotal.length() > 2 ? msgidtotal.substring(msgidtotal.length() - 2) : msgidtotal;

                throw new ServerApiError(result.get("error").getAsString());

            } else {
                throw new ServerApiError("Unknown response");
            }
        } else {
            List<CoreConnection> connectionList = new ArrayList<CoreConnection>();
            JsonArray result = new JsonParser().parse(body).getAsJsonArray();
            result.forEach((dt) ->
            {
                if (dt.isJsonObject())
                {
                    JsonObject connectionjson = dt.getAsJsonObject();
                    connectionList.add(connectionFromJSON(connectionjson));

                }
            });
            return connectionList;
        }
    }

    public static CoreConnection newConnection(InetSocketAddress ip, UUID minecraft_uuid, String minecraft_username, CoreKey key) throws IOException, ServerApiError {

        String postip = ip.getAddress().getHostAddress();
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

            CoreConnection connection = connectionFromJSON(result);
            return connection;

        }

    }

    public static List<CoreAdvancement> getAdvancements(UUID minecraft_uuid, CoreKey key) throws IOException, ServerApiError {

        String postuuid = minecraft_uuid.toString();
        String postkey = key.getHash();

        URL url = new URL("https://api.purecore.io/rest/1/advancement/list/");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("uuid", postuuid);
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


        JsonElement result = new JsonParser().parse(body);

        if(result.isJsonObject()){
            if(result.getAsJsonObject().has("error")){

                String msgidtotal = Long.toString(Instant.now().toEpochMilli());
                String msgid = msgidtotal.length() > 2 ? msgidtotal.substring(msgidtotal.length() - 2) : msgidtotal;

                throw new ServerApiError(result.getAsJsonObject().get("error").getAsString());

            } else {

                throw new ServerApiError("Unexpected type (expecting list, got object");

            }
        } else {

            return advancementsFromJson(result.getAsJsonArray());

        }

    }

    public static boolean pushAdvancement(UUID minecraft_uuid, CoreAdvancement advancement, CoreKey key) throws IOException, ServerApiError {

        String postuuid = minecraft_uuid.toString();
        String advancementkey = advancement.getAdvancementName();
        Long advancementdate = advancement.getDate().toInstant().toEpochMilli();
        String postkey = key.getHash();

        URL url = new URL("https://api.purecore.io/rest/1/advancement/add/");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("uuid", postuuid);
        params.put("key", postkey);
        params.put("advancementkey", advancementkey);
        params.put("epoch", advancementdate.toString());

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

        if(result.getAsJsonObject().has("error")){

            String msgidtotal = Long.toString(Instant.now().toEpochMilli());
            String msgid = msgidtotal.length() > 2 ? msgidtotal.substring(msgidtotal.length() - 2) : msgidtotal;

            throw new ServerApiError(result.getAsJsonObject().get("error").getAsString());

        } else {

            return true;

        }

    }

    public static boolean pushAdvancementList(UUID minecraft_uuid, List<CoreAdvancement> advancements, CoreKey key) throws IOException, ServerApiError {

        ArrayList<HashMap> json_list = new ArrayList<>();

        for (CoreAdvancement advancement:advancements) {
            HashMap object = new HashMap();
            object.put("key",advancement.getAdvancementName());
            object.put("epoch",advancement.getDate().toInstant().toEpochMilli());
            json_list.add(object);
        }

        String json = new Gson().toJson(json_list);

        String postuuid = minecraft_uuid.toString();
        String postkey = key.getHash();

        URL url = new URL("https://api.purecore.io/rest/1/advancement/list/add/");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("uuid", postuuid);
        params.put("key", postkey);
        params.put("advancements", json);

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

        JsonElement result = new JsonParser().parse(body);
        if(result.isJsonObject()){
            if(result.getAsJsonObject().has("error")){

                String msgidtotal = Long.toString(Instant.now().toEpochMilli());
                String msgid = msgidtotal.length() > 2 ? msgidtotal.substring(msgidtotal.length() - 2) : msgidtotal;

                throw new ServerApiError(result.getAsJsonObject().get("error").getAsString());

            } else {

                throw new ServerApiError("Unexpected type (expecting list, got object");

            }
        } else {
            return true;
        }

    }

}
