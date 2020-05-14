package io.purecore.mcplugin.spigot.events;

import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.punishment.Offence;
import io.purecore.api.punishment.Punishment;
import io.purecore.api.user.Player;
import io.purecore.mcplugin.spigot.Main;
import io.purecore.mcplugin.spigot.tasks.MarkPending;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static io.purecore.mcplugin.spigot.Main.playerElements;

public class Inventory implements Listener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event){
        if (Main.punishmentGUIs.contains(event.getInventory())) {
            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_LAND,10,1);
        }
    }

    @EventHandler
    public void onInventoryClickItem(InventoryClickEvent event) {
        onInventoryInteract(event, event.getCurrentItem());
    }

    @EventHandler
    public void onInventoryDragItem(InventoryDragEvent event) {
        onInventoryInteract(event, null);
    }

    private void onInventoryInteract(InventoryInteractEvent event, ItemStack item) {
        if (Main.punishmentGUIs.contains(event.getInventory())) {
            event.setCancelled(true);
            if(item!=null){

                List<String> lore = Objects.requireNonNull(item.getItemMeta()).getLore();
                assert lore != null;
                List<Offence> offenceList = Main.selectedOffences.get(event.getInventory());

                if(lore.size()>0){
                    if(lore.get(lore.size()-1).contains("#")){
                        Offence offence = new Offence(lore.get(lore.size()-1).replace("#",""), Offence.Type.UNK,null,lore.get(0),lore.get(1),-1);

                        boolean found = false;
                        int pos = -1;

                        for (Offence offenceInList:Main.selectedOffences.get(event.getInventory())) {
                            pos++;
                            if (offenceInList.getId().equals(offence.getId())) {
                                found = true;
                                break;
                            }
                        }

                        ItemStack[] contents = event.getInventory().getContents();
                        int itemPosition = 0;

                        for (ItemStack itemToReplace : contents) {
                            String title = Objects.requireNonNull(itemToReplace.getItemMeta()).getDisplayName();
                            if(title.equals(item.getItemMeta().getDisplayName())){
                                break;
                            } else {
                                itemPosition++;
                            }
                        }

                        if(found){
                            event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,10,0.2f);
                            offenceList.remove(pos);
                            ItemStack newItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
                            newItem.setItemMeta(item.getItemMeta());
                            event.getInventory().setItem(itemPosition,newItem);

                        } else {
                            event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,10,1);
                            offenceList.add(offence);
                            ItemStack newItem = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE, 1);
                            newItem.setItemMeta(item.getItemMeta());
                            event.getInventory().setItem(itemPosition,newItem);
                        }
                    } else if(lore.get(lore.size()-1).contains("%cancel")){
                        event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_CHEST_CLOSE,10,1);
                        event.getWhoClicked().closeInventory();
                        playerElements.remove(event.getInventory());
                    } else if(lore.get(lore.size()-1).contains("%execute")){

                        event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE,10,1);
                        event.getWhoClicked().closeInventory();

                        Player player = playerElements.get(event.getInventory()).get(0);
                        Player moderator = playerElements.get(event.getInventory()).get(1);

                        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Punishment punishment = new Punishment(Main.core,player,moderator,offenceList);
                                    event.getWhoClicked().sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"→"+ChatColor.GRAY+" Created punishment successfully");
                                    MarkPending checkPending = new MarkPending(Main.core);
                                    checkPending.run();
                                } catch (ApiException | IOException | CallException e) {
                                    event.getWhoClicked().sendMessage(ChatColor.RED+"✘"+ChatColor.GRAY+" "+e.getMessage());
                                    event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_BREAK,10,1);
                                }
                            }
                        });

                        playerElements.remove(event.getInventory());
                    }
                }
            }
        }


    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event){
        Main.punishmentGUIs.remove(event.getInventory());
        Main.selectedOffences.remove(event.getInventory());
    }

}
