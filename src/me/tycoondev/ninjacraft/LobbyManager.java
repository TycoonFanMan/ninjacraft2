package me.tycoondev.ninjacraft;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Created by Chase on 6/2/2016.
 */
public class LobbyManager implements Listener {

    private static LobbyManager manager = new LobbyManager();

    private Plugin plugin;

    private Location spawn;

    private LobbyManager(){

    }

    public void setup(Plugin p){
        SettingsManager sm = SettingsManager.getManager();
        if(!sm.getConfig().contains("lobby.spawn")){
            sm.getConfig().set("lobby.spawn", null);
            sm.saveConfig();
            spawn = null;
        }
        else{
            spawn = Location.deserialize(sm.getConfig().getConfigurationSection("lobby.spawn").getValues(false));
        }

    }

    public static LobbyManager getManager(){
        return manager;
    }

    public void setLobbySpawn(Location l){
        spawn = l;
        SettingsManager.getManager().getConfig().set("lobby.spawn", l.serialize());
        SettingsManager.getManager().saveConfig();
    }

    public Location getLobbySpawn(){
        return spawn;
    }
}
