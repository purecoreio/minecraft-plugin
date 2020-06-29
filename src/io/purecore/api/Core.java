package io.purecore.api;

import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.instance.Instance;
import io.purecore.api.key.Key;
import io.purecore.api.sockets.CoreSocket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class Core {

    private String key;

    public Core(String key) {
        this.key=key;
    }

    public Core() {
    }

    public Core(Key key) {
        this.key=key.getHash();
    }

    public String getKey() {
        return key;
    }

    public Key getKeyLegacy(){
        return new Key(this.key);
    }

    public Core getCore(){
        return this;
    }

    public Instance getInstance() throws ApiException, IOException, CallException {
        return new Instance(this);
    }

    public CoreSocket getSocket() throws URISyntaxException {
        return new CoreSocket(this);
    }

    public CoreSocket getSocket(Logger logger) throws URISyntaxException {
        return new CoreSocket(this, logger);
    }
}
