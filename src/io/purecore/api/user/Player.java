package io.purecore.api.user;

import com.google.gson.JsonObject;
import io.purecore.api.Core;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.punishment.Offence;
import io.purecore.api.punishment.Punishment;
import io.purecore.api.request.ObjectRequest;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class Player extends Core {

    private Core core;
    private String coreid;
    private String username;
    private UUID uuid;
    private boolean verified;

    public Player(Core core, String username, UUID uuid, boolean verified)
    {

        super(core.getKey());
        this.core=core;
        this.username = username;
        this.uuid = uuid;
        this.verified = verified;

    }

    public Player(Core core, String username) throws ApiException, IOException, CallException {

        super(core.getKey());
        this.core=core;
        this.username = username;

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("username", username);

        JsonObject playerResult = new ObjectRequest(core, ObjectRequest.Call.PLAYER_FROM_USERNAME, params).getResult();

        this.coreid = playerResult.get("coreid").getAsString();
        this.uuid = UUID.fromString(playerResult.get("uuid").getAsString());
        this.verified = playerResult.get("verified").getAsBoolean();

    }

    public Punishment punish(Player player, List<Offence> offenceList) throws ApiException, IOException, CallException {
        return new Punishment(this.core, player, this, offenceList);
    }

    public String getCoreid() {
        return coreid;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUUID(){
        return uuid;
    }

}
