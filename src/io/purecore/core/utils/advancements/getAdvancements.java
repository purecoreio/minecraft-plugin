package io.purecore.core.utils.advancements;

import io.purecore.core.api.Core;
import io.purecore.core.api.type.CoreAdvancement;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class getAdvancements {


    public static List<Advancement> getAdvancementList(Plugin plugin, Player player){

        Iterator<Advancement> advancementIterator = plugin.getServer().advancementIterator();

        List<Advancement> playerAdvancements = new ArrayList<>();

        for (; advancementIterator.hasNext(); ) {
            Advancement advancement = advancementIterator.next();
            AdvancementProgress progress = Objects.requireNonNull(player.getPlayer()).getAdvancementProgress(advancement);
            if(progress.isDone()){

                playerAdvancements.add(advancement);

            }
        }

        return playerAdvancements;

    }

}
