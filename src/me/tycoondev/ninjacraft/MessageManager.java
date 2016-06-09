package me.tycoondev.ninjacraft;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 * Created by Chase on 6/3/2016.
 */
public class MessageManager {

    public static MessageManager manager = new MessageManager();

    private Plugin p;

    private HashMap<PrefixType, String> prefix =  new HashMap<>();

    private MessageManager(){ }

    public static MessageManager getManager(){
        return manager;
    }

    public void setup(Plugin p){

        FileConfiguration f = SettingsManager.getManager().getMessageConfig();
        prefix.put(PrefixType.MAIN, replaceColors(f.getString("prefix.main")));
        prefix.put(PrefixType.WARNING, replaceColors(f.getString("prefix.types.warning")));
        prefix.put(PrefixType.INFO, replaceColors(f.getString("prefix.types.info")));
        prefix.put(PrefixType.ERROR, replaceColors(f.getString("prefix.types.error")));

    }

    public void sendMessage(PrefixType prefixType, String msg, Player player){
        player.sendMessage(prefix.get(PrefixType.MAIN) + " " + prefix.get(prefixType) + msg);
    }

    public void broadcastMessage(PrefixType prefixType, String msg){
        Bukkit.broadcastMessage(prefix.get(PrefixType.MAIN) + " " + prefix.get(prefixType) + msg);
    }

    private String replaceColors(String s){
        if(s == null){
            return null;
        }
        return s.replaceAll("(&([a-fk-or0-9]))", "\u00A7$2");
    }


}


