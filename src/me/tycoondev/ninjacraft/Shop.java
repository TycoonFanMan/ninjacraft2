package me.tycoondev.ninjacraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Chase on 6/16/2016.
 */
public class Shop {
    private HashMap<ItemStack, Integer> items;
    private Inventory shop;
    private int rows;
    private FileConfiguration shopsConfig = SettingsManager.getManager().getShopConfig();

    public Shop(String name) {
        if (!shopsConfig.contains("shops." + name)) {
            Bukkit.getServer().getLogger().warning("Failed to load " + name + " shop from config: does not exist");

        } else {
            items = new HashMap<>();
            ConfigurationSection shops = shopsConfig.getConfigurationSection("shops." + name);
            if (shops.contains("items")) {
                for(String s: shops.getConfigurationSection("items").getKeys(false)){
                    //s is the name
                    int price = shops.getInt("items." + s + ".price");
                    int amount = shops.getInt("items." + s + ".amount");
                    items.put(createItem(s, amount, price), price);
                }
            }
            shop = Bukkit.createInventory(null, getBestSize(), ChatColor.BLUE + name + " Shop");
            shop.setContents(getItems());

        }

    }

    private ItemStack[] getItems(){
        ItemStack[] tmpItems = new ItemStack[shop.getSize()];
        for(int i = 0; i < 9; i++){
            tmpItems[i] = null;
        }
        for(int i = tmpItems.length - 9; i < tmpItems.length; i++){
            tmpItems[i] = null;
        }
        int size = tmpItems.length - 18;
        if(rows == 0){
            int start = ((size - items.keySet().size()) / 2 ) + 9;
            for(ItemStack it: items.keySet()){
                tmpItems[start++] = it;
            }
        }
        else if(rows > 0){
            Iterator keys = items.keySet().iterator();
            for(int i = 0; i < rows; i++){
                int start = i * (9) + 9;
                while(start < i * 9 + 18){
                    tmpItems[start++] = (ItemStack) keys.next();
                }
            }
            int start = (size - items.keySet().size()) / 2 + 9 + (9 * rows);
            while(keys.hasNext()){
                tmpItems[start++] = (ItemStack) keys.next();
            }
        }

        return tmpItems;
    }

    private int getBestSize(){
        rows = (items.keySet().size() / 9);
        return (rows + 3) * 9;
    }

    private ItemStack createItem(String name, int amount, int price){
        ItemStack i = new ItemStack(Material.getMaterial(name), amount);
        ItemMeta meta = i.getItemMeta();
        meta.setLore(Arrays.asList(ChatColor.GREEN + "Cost: " + ChatColor.GOLD + price ));
        i.setItemMeta(meta);
        return i;

    }

    public void openShop(Player p){
        p.openInventory(shop);
    }
}
