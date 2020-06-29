package io.purecore.api.sockets;

import io.purecore.api.Core;
import io.purecore.api.connection.ConnectionDestroyRequest;
import io.purecore.api.connection.ConnectionRequest;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoreSocket extends Core {

    Socket socket;
    boolean ready = false;
    Logger logger = null;

    public CoreSocket(Core core, Logger logger) throws URISyntaxException {
        super(core.getKeyLegacy());
        this.logger=logger;
        this.init();
    }

    public CoreSocket(Core core) throws URISyntaxException {
        super(core.getKeyLegacy());
        this.init();
    }

    // SOCKET AUTH

    private void init() throws URISyntaxException {
        this.socket = IO.socket("http://localhost:3000");
        socket.on("handshake", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ready=true;
                if(logger!=null){
                    logger.log(Level.INFO,"Authenticated with the socket server");
                    createSession("quiquelhappy","8a0b22c3-f85a-4465-b0ea-732fcbcf1e86","83.50.58.159");
                }
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ready=false;
                if(logger!=null){
                    logger.log(Level.WARNING,"Disconnected from socket server");
                }
            }
        }).on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                giveHand();
                if(logger!=null){
                    logger.log(Level.INFO,"Connected with the socket server");
                }
            }
        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(logger!=null){
                    logger.log(Level.WARNING,"Error while connecting to the socket server: "+ Arrays.toString(args));
                }
            }
        }).on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(logger!=null){
                    logger.log(Level.WARNING,"Error while reconnecting to the socket server: "+ Arrays.toString(args));
                }
            }
        }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(logger!=null){
                    logger.log(Level.WARNING,"Error related to the socket server: "+ Arrays.toString(args));
                }
            }
        }).on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(logger!=null){
                    logger.log(Level.INFO,"Connecting to the socket server");
                }
            }
        }).on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(logger!=null){
                    logger.log(Level.INFO,"Trying to reconnect to the socket server");
                }
            }
        }).on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(logger!=null){
                    logger.log(Level.WARNING,"Error while trying to reconnect to the socket server: "+ Arrays.toString(args));
                }
            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(logger!=null){
                    logger.log(Level.WARNING,"Timeout while connecting to the socket server");
                }
            }
        }).on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(logger!=null){
                    logger.log(Level.INFO,"Reconnecting to the socket server");
                }
            }
        });
        socket.connect();
    }

    private void giveHand(){
        if(!ready){
            socket.emit("handshakeRequest",this.getKey());
        }
    }

    public void goodbye(){
        if(socket.connected()){
            socket.io().reconnection(false);
            socket.disconnect();
            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    socket = null;
                }
            });
        }
    }

    // SESSIONS

    public void createSession(String username, UUID uuid, InetSocketAddress address){
        if(ready){
            socket.emit("createConnection",new ConnectionRequest(username,uuid.toString(),address.getAddress().getHostAddress()).toJson());
        }
    }

    public void createSession(String username, String id, String ip){
        if(ready){
            socket.emit("createConnection",new ConnectionRequest(username,id,ip).toJson());
        }
    }

    public void destroySessions(UUID uuid){
        if(ready){
            socket.emit("destroySessions",new ConnectionDestroyRequest(uuid.toString()));
        }
    }

    public void destroySessions(String uuid){
        if(ready){
            socket.emit("destroySessions",new ConnectionDestroyRequest(uuid));
        }
    }



}
