package io.purecore.plugin.spigot.gui;

import io.purecore.api.Core;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.instance.Instance;
import io.purecore.api.punishment.Offence;
import io.purecore.plugin.spigot.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreatePunishment implements InventoryHolder, Listener {

    private Inventory inv;
    private int usedSlots = 0;

    public CreatePunishment() {
        inv = Bukkit.createInventory(this, 36, ChatColor.RED + "" + ChatColor.BOLD + "Core: " + ChatColor.RESET + ChatColor.DARK_GRAY + "Create a punishment");
        Main.punishmentGUIs.add(inv);
        Main.selectedOffences.put(inv,new ArrayList<Offence>());
        loadData(Main.instance);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public void loadData(Instance instance){

        // offence panels
        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {
            @Override
            public void run() {

                try {
                    List<Offence> offenceList = instance.asNetwork().getOffences();

                    for (int i = 0; i < offenceList.size(); i++) {

                        inv.setItem(i, createGuiItem(offenceList.get(i)));

                    }
                } catch (ApiException | IOException | CallException e) {
                    e.printStackTrace();
                }

                // fill empty

                ItemStack emptyPlaceholder = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                ItemMeta placeholderMeta = emptyPlaceholder.getItemMeta();
                assert placeholderMeta != null;
                placeholderMeta.setDisplayName(ChatColor.DARK_GRAY  + "?");
                placeholderMeta.setLore(new ArrayList<String>());
                emptyPlaceholder.setItemMeta(placeholderMeta);

                for (int i = 0; i < inv.getSize(); i++) {
                    try{
                        if(inv.getItem(i).getType()==Material.AIR){
                            inv.setItem(i,emptyPlaceholder);
                        }
                    } catch (NullPointerException e){
                        inv.setItem(i,emptyPlaceholder);
                    }
                }

                // controls

                ItemStack executeControl = new ItemStack(Material.LIME_STAINED_GLASS_PANE,1);
                ItemMeta executeControlMeta = executeControl.getItemMeta();
                assert executeControlMeta != null;
                executeControlMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN+ChatColor.BOLD + "Execute");
                List<String> executeLore = new ArrayList<>();
                executeLore.add("%execute");
                executeControlMeta.setLore(executeLore);
                executeControl.setItemMeta(executeControlMeta);
                inv.setItem(35,executeControl);

                ItemStack cancelControl = new ItemStack(Material.RED_STAINED_GLASS_PANE,1);
                ItemMeta cancelControlMeta = cancelControl.getItemMeta();
                assert cancelControlMeta != null;
                cancelControlMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.RED+ChatColor.BOLD + "Cancel");
                List<String> cancelLore = new ArrayList<>();
                cancelLore.add("%cancel");
                cancelControlMeta.setLore(cancelLore);
                cancelControl.setItemMeta(cancelControlMeta);
                inv.setItem(34,cancelControl);
            }
        });

    }

    private ItemStack createGuiItem(Offence offence) {

        // item

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        // name and desc

        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.BOLD + offence.getName() + " " + ChatColor.RED + ChatColor.ITALIC + "-" + offence.getNegativePoints());
        ArrayList<String> metalore = new ArrayList<String>();
        
        String description = offence.getDescription();
        int numberOfLines = (int) Math.ceil((float) description.length() / (float) 20);
        for (int i = 0; i < numberOfLines; i++) {
            if(description.length()<i*20+20){
                metalore.add(ChatColor.GRAY + description.substring(i*20).trim());
            } else {
                metalore.add(ChatColor.GRAY + description.substring(i*20,i*20+20).trim());
            }
        }

        metalore.add("Type → " + offence.getType().toString());
        metalore.add("#" + offence.getId());

        // set props

        meta.setLore(metalore);
        item.setItemMeta(meta);
        return item;
    }
}