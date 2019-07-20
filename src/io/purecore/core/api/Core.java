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
import org.apache.http.util.EntityUtils;

import java.io.IOException;
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

        params.add(new BasicNameValuePair("ip", postip));
        params.add(new BasicNameValuePair("uuid", postuuid));
        params.add(new BasicNameValuePair("username", minecraft_username));
        params.add(new BasicNameValuePair("key", postkey));

        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        String responseString = EntityUtils.toString(entity, "UTF-8");
        System.out.println(responseString);

        return null;
    }

}
