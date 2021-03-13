package io.purecore.mcplugin.spigot.events;

import io.purecore.api.call.ApiException;
import io.purecore.api.punishment.PunishmentType;
import io.purecore.mcplugin.API;
import io.purecore.mcplugin.common.thirdPartyIntegration.LiteBans;
import litebans.api.Entry;
import litebans.api.Events;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;

import java.io.IOException;
import java.util.logging.Level;

public class LiteBansHandler extends Events.Listener {

    private Plugin plugin;

    public LiteBansHandler(Plugin plugin){
        this.plugin=plugin;
        this.plugin.getLogger().log(Level.INFO,"Hooked into LiteBans");

        // no need for scheduler, this block is already async
        Events.get().register(new Events.Listener() {
            @Override
            public void entryRemoved(Entry entry){
                try {
                    LiteBans.handleEntryDelete(entry);
                } catch (JSONException | ApiException | IOException e) {
                    plugin.getLogger().log(Level.WARNING, "There was an error while removing a LiteBans punishment: " + e.getMessage());
                }
            }

            @Override
            public void entryAdded(Entry entry) {
                try {
                    LiteBans.handleEntryAdd(entry);
                } catch (JSONException | ApiException | IOException e) {
                    plugin.getLogger().log(Level.WARNING, "There was an error while creating a LiteBans punishment: " + e.getMessage());
                }
            }
        });


    }

}
