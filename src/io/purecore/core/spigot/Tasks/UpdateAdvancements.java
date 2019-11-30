package io.purecore.core.spigot.Tasks;

import io.purecore.core.api.Core;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.CoreAdvancement;
import io.purecore.core.api.type.CoreKey;
import io.purecore.core.spigot.Main;
import io.purecore.core.utils.console.Msgs;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.*;


public class UpdateAdvancements implements Runnable {


    private Plugin plugin;
    private CoreKey key;
    private Player player;
    private List<Advancement> advancements;

    public UpdateAdvancements(Plugin plugin, CoreKey key, Player player, List<Advancement> advancements){

        this.plugin = plugin;
        this.key = key;
        this.player = player;
        this.advancements = advancements;

    }

    @Override
    public void run() {

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    List<CoreAdvancement> advancementlist = Core.getAdvancements(player.getUniqueId(), key);
                    List<String> advancementKeys = new ArrayList<>();

                    for (CoreAdvancement advancement: advancementlist) {
                        advancementKeys.add(advancement.getAdvancementName());
                    }

                    List<CoreAdvancement> pendingAdvancements = new ArrayList<>();

                    for (Advancement advancement:advancements) {
                        AdvancementProgress progress = Objects.requireNonNull(player.getPlayer()).getAdvancementProgress(advancement);
                        if(!advancementKeys.contains(advancement.getKey().toString())){

                            Collection<String> criteriaList = progress.getAwardedCriteria();
                            Date dateAwarded = null;
                            for (String criteria:criteriaList) {
                                if(dateAwarded==null||dateAwarded.toInstant().toEpochMilli() < Objects.requireNonNull(progress.getDateAwarded(criteria)).toInstant().toEpochMilli()){
                                    dateAwarded=progress.getDateAwarded(criteria);
                                }
                            }

                            pendingAdvancements.add(new CoreAdvancement(advancement.getKey().toString(), dateAwarded ));

                        }
                    }

                    if(Main.debug){
                        Msgs.showWarning(Main.logger,"advancements","pushing "+pendingAdvancements.size()+" advancements");
                    }

                    Core.pushAdvancementList(player.getUniqueId(),pendingAdvancements,key);

                } catch (IOException | ServerApiError e) {
                    Msgs.showError(Main.logger,"advancements","error while pushing pending advancements: "+e.getMessage());
                }
            }
        });

    }
}
