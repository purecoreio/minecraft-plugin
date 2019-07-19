package io.purecore.core.api;

import com.google.gson.JsonObject;
import io.purecore.core.api.type.CoreKey;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Connection {

    public static JsonObject newConnection(InetAddress ip, UUID minecraft_uuid, String minecraft_username, CoreKey key) throws IOException {

        String postip = ip.getHostAddress();
        String postuuid = minecraft_uuid.toString();
        String postkey = key.getHash();

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://api.purecore.io/rest/1/connection/new/");

        List<NameValuePair> params = new ArrayList<NameValuePair>(2);

        params.add(new BasicNameValuePair("ip", "12345"));
        params.add(new BasicNameValuePair("uuid", "Hello!"));
        params.add(new BasicNameValuePair("username", "Hello!"));
        params.add(new BasicNameValuePair("key", "Hello!"));

        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream instream = entity.getContent()) {


                // TO-DO
                return null;


            }
        }

    }

}
