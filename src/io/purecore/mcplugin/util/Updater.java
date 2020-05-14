package io.purecore.mcplugin.util;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Updater {

    String localVersion;
    String folder;

    public Updater(String localVersion){
        this.localVersion=localVersion;
        this.folder="plugins";
    }

    public Updater(String localVersion, String folder){
        this.localVersion=localVersion;
        this.folder=folder;
    }

    public void startUpdateProcedure(Logger logger){
        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(new URL("https://api.spigotmc.org/legacy/update.php?resource=62575").toURI()).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String spigotPluginVersion = response.body();

            if (!spigotPluginVersion.isEmpty() && !spigotPluginVersion.equals(localVersion)) {

                logger.log(Level.INFO,"Got a new version ["+spigotPluginVersion+"], starting update procedure");
                byte[] data = this.downloadUpdatedJar();
                this.replaceTheJar(data);
                logger.log(Level.INFO,"The plugin has been downloaded and updated successfully, reload the server to use the last version");

            } else {
                logger.log(Level.INFO,"You're using the last version");
            }

        } catch(RuntimeException e) {
            logger.log(Level.SEVERE,"Error while updating the final jar: "+e.getMessage());
        } catch (IOException | URISyntaxException | InterruptedException e) {
            logger.log(Level.WARNING,"Error while checking for updates: "+e.getMessage());
        }
    }

    public byte[] downloadUpdatedJar() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://dl.purecore.io/mc/purecore.jar")).build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        InputStream input = response.body();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len;

        while ((len = input.read(buff)) != -1) {
            os.write(buff, 0, len);
        }
        return os.toByteArray();

    }

    public void replaceTheJar(byte[] data) throws RuntimeException, IOException {
        File pluginFolder = new File(this.folder);

        if (!pluginFolder.exists()) {
            throw new RuntimeException("couldn't fix the "+this.folder+" folder");
        }

        if (!pluginFolder.isDirectory()) {
            throw new RuntimeException(this.folder+" isn't a valid folder");
        }

        File[] plugins = pluginFolder.listFiles();
        if (plugins == null) {
            throw new IOException("couldn't list the folder contents");
        }

        File purecore = null;
        for (File plugin : plugins) {
            if (!plugin.getName().equals("purecore")) {
                continue;
            }
            purecore = plugin;
            break;
        }
        if (purecore == null) {
            throw new RuntimeException("Failed to get the current purecore file");
        }

        OutputStream outputStream = new FileOutputStream(purecore, false);
        outputStream.write(data);
        outputStream.flush();
        outputStream.close();
    }

}
