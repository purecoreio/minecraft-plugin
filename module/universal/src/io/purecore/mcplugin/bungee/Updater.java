package io.purecore.mcplugin.bungee;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.purecore.api.versioning.Version;
import net.md_5.bungee.api.plugin.Plugin;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.UUID;

public class Updater {

    Plugin plugin;

    public Updater(Plugin plugin){
        this.plugin=plugin;
    }

    public byte[] update(Version version) throws IOException, URISyntaxException {
        String url = "https://github.com/purecoreio/minecraft-plugin/releases/download/"+version.getTag()+"/"+version.getFileName();
        URLConnection connection = new URL(url).openConnection();
        InputStream is = connection.getInputStream();

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buff = new byte[1024];
            int len;
            long downloaded = 0;
            if (is == null) {
                throw new IOException("Failed downloading update: Cannot open connection with remote server.");
            }
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
                downloaded += len;
            }
            is.close();
            byte[] file = os.toByteArray();
            os.close();
            return file;
        }
    }

    public boolean checkIntegrity(Version version, long size) throws Exception {

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://api.github.com/repos/purecoreio/minecraft-plugin/releases/tags/"+version.getTag()).newBuilder());
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.body() != null) {
            JsonElement body = new Gson().fromJson(response.body().string(), JsonElement.class);
            if(response.code()==200 || response.code()==302){
                JsonObject release = body.getAsJsonObject();
                JsonArray assets = release.get("assets").getAsJsonArray();
                for (JsonElement assetElement : assets) {
                    JsonObject asset = assetElement.getAsJsonObject();
                    if(asset.get("name").getAsString().equals(version.getFileName())){
                        if(asset.get("size").getAsLong()!=size){
                            return false;
                        } else {
                            return true;
                        }
                    }
                    break;
                }
                throw new Exception("Couldn't find target asset to compare with");
            } else {
                throw new Exception("Invalid response code");
            }
        } else {
            throw new Exception("Empty body");
        }
    }

    public void install(byte[] bytes) throws IOException {
        File pluginFolder = new File("plugins");
        if (!pluginFolder.exists()) {
            throw new RuntimeException("Can't find the plugins folder.");
        }
        if (!pluginFolder.isDirectory()) {
            throw new RuntimeException("Plugins not a folder.");
        }
        File[] plugins = pluginFolder.listFiles();
        if (plugins == null) {
            throw new IOException("Can't get the files in plugins folder");
        }
        File newJar = new File(pluginFolder, "purecore" + UUID.randomUUID().toString().replace("-", "") + ".jar");

        File pluginJar = this.plugin.getProxy().getPluginManager().getPlugin("purecore").getFile();
        if (!pluginJar.delete()) {
            // couldn't delete, replace
            try (OutputStream outputStream = new FileOutputStream(pluginJar, false)) {
                outputStream.write(bytes);
                outputStream.flush();
            }
        } else {
            try (OutputStream outputStream = new FileOutputStream(newJar, false)) {
                outputStream.write(bytes);
                outputStream.flush();
            }
        }
    }

}
