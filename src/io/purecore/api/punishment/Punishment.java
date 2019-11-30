package io.purecore.api.punishment;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.purecore.api.Core;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.user.Player;
import io.purecore.api.request.ObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Punishment extends Core {

    public Punishment(Core core, Player player, Player moderator, List<Offence> offenceList) throws ApiException, IOException, CallException {

        super(core.getKey());

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("player", player.getCoreid());
        params.put("moderator", moderator.getCoreid());


        List<String> offenceIdList = new ArrayList<>();
        for (Offence offence:offenceList) {
            if(!offenceIdList.contains(offence.getId())){
                offenceIdList.add(offence.getId());
            }
        }

        Gson gson = new Gson();
        params.put("offenceList", gson.toJson(offenceIdList));

        JsonObject punishmentResult = new ObjectRequest(core, ObjectRequest.Call.CREATE_PUNISHMENT, params).getResult();

    }
}
