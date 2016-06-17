package me.tycoondev.ninjacraft;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Chase on 5/24/2016.
 */
public class SettingsManager {

    private SettingsManager() { }

    private static SettingsManager manager = new SettingsManager();

    public static SettingsManager getManager(){
        return manager;
    }

    Plugin plugin;

    private FileConfiguration config;
    private File configFile;

    private FileConfiguration arenas;
    private File arenasConfig;

    private FileConfiguration inventories = null;
    private File invConfig = null;

    private FileConfiguration messages = null;
    private File messageConfig = null;

    private FileConfiguration shops = null;
    private File shopConfig = null;

    public void setup(Plugin p){
        plugin = p;
        configFile = new File(p.getDataFolder(), "config.yml");
        config = p.getConfig();
        saveConfig();

        if(!p.getDataFolder().exists()){
            try{
                p.getDataFolder().createNewFile();
            }
            catch (IOException e){
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create NinjaCraft folder!");
            }
        }

        arenasConfig = new File(p.getDataFolder(), "arenas.yml");

        if(!arenasConfig.exists()){
            try {
                arenasConfig.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create arenas config!");
            }
        }

        arenas = YamlConfiguration.loadConfiguration(arenasConfig);
        saveArenaConfig();


        invConfig = new File(p.getDataFolder(), "inventories.yml");

        if(!invConfig.exists()){
            plugin.saveResource("inventories.yml", true);
        }

        inventories = YamlConfiguration.loadConfiguration(invConfig);
        saveInvConfig();



        messageConfig = new File(p.getDataFolder(), "messages.yml");

        if(!messageConfig.exists()){
            plugin.saveResource("messages.yml", true);
        }

        messages = YamlConfiguration.loadConfiguration(messageConfig);
        saveMessageConfig();

        shopConfig = new File(p.getDataFolder(), "shops.yml");

        if(!shopConfig.exists()){
            plugin.saveResource("shops.yml", true);

        }

        shops = YamlConfiguration.loadConfiguration(shopConfig);
        saveShopConfig();
    }

    public FileConfiguration getShopConfig(){
        return shops;
    }

    public void saveShopConfig(){
        try{
            shops.save(shopConfig);
        } catch (IOException e){
            Bukkit.getServer().getLogger().warning("Could not save shops config!");
        }
    }

    public void saveInvConfig(){
        try{
            inventories.save(invConfig);
        } catch (IOException e){
            Bukkit.getServer().getLogger().warning("Could not save inventories config!");
        }
    }

    public void saveMessageConfig() {
        try{
            messages.save(messageConfig);
        } catch (IOException e){
            Bukkit.getServer().getLogger().warning("Could not save message config!");
        }
    }

    public FileConfiguration getMessageConfig() { return messages; }

    public FileConfiguration getInvConfig() { return inventories; }

    public FileConfiguration getArenasConfig(){
        return arenas;
    }

    public void saveArenaConfig(){
        try{
            arenas.save(arenasConfig);
        }
        catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save Arenas.yml");
        }
    }
    public void reloadArenaConfig(){
        arenas = YamlConfiguration.loadConfiguration(arenasConfig);
    }

    public FileConfiguration getConfig(){
        return config;
    }

    public void saveConfig(){
        try{
            config.save(configFile);
        }
        catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml");
        }
    }
    public void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}
