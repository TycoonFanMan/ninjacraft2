package me.tycoondev.ninjacraft;

import me.tycoondev.ninjacraft.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chase on 5/24/2016.
 */
public class ArenaManager {

    private final ArrayList<Arena> arenas = new ArrayList<Arena>();
    private int arenaSize = 0;

    private Plugin p;

    private MessageManager msgr;

    private ArenaManager() {}

    private static ArenaManager manager = new ArenaManager();

    public static ArenaManager getManager(){
        return manager;
    }

    public void setup(Plugin p){
        this.p = p;
        msgr = MessageManager.getManager();
        reloadArenas();

    }

    private void reloadArenas(){

        arenas.clear();
        arenaSize = 0;

        if(SettingsManager.getManager().getArenasConfig().getIntegerList("arenas.arenas").isEmpty()){
            return;
        }

        for(int i : SettingsManager.getManager().getArenasConfig().getIntegerList("arenas.arenas")){
            Arena a = new Arena(arenaSize,
                    deSerializeLoc(SettingsManager.getManager().getArenasConfig().getString("arenas." + arenaSize + ".max")),
                    deSerializeLoc(SettingsManager.getManager().getArenasConfig().getString("arenas." + arenaSize + ".min")));

            arenas.add(a);
            arenaSize++;
        }
    }

    public Arena getArena(int id){
        for(Arena a : arenas){
            if(a.getID() == id){
                return a;
            }
        }
        return null; //not Found;
    }

    public Arena createArena(Location max, Location min){
        Arena a = new Arena(arenaSize,max,min);
        arenaSize++;
        arenas.add(a);

        SettingsManager.getManager().getArenasConfig().set("arenas." + a.getID() + ".max", serializeLoc(a.getMax()));
        SettingsManager.getManager().getArenasConfig().set("arenas." + a.getID() + ".min", serializeLoc(a.getMin()));

        List<Integer> list = SettingsManager.getManager().getArenasConfig().getIntegerList("arenas.arenas");
        list.add(a.getID());
        SettingsManager.getManager().getArenasConfig().set("arenas.arenas", list);
        SettingsManager.getManager().saveArenaConfig();

        return a;
    }

    public boolean removeArena(int arenaID){
        Arena a = getArena(arenaID);

        if(a == null){
            return false;
        }

        arenas.remove(a);

        SettingsManager.getManager().getArenasConfig().set("arenas." + arenaID, null);

        List<Integer> list = SettingsManager.getManager().getArenasConfig().getIntegerList("arenas.arenas");
        list.remove(a);
        SettingsManager.getManager().getArenasConfig().set("arenas.arenas", list);
        SettingsManager.getManager().saveArenaConfig();
        return true;

    }

    //Add player to specified arena
    public void addPlayer(Player player, int id){

        FileConfiguration inven = SettingsManager.getManager().getInvConfig();

        if(isInGame(player)){
            msgr.sendMessage(PrefixType.ERROR, "You must leave the current arena before joining another!", player);
            msgr.sendMessage(PrefixType.INFO, "Type /nc leave to leave!", player);
            return;
        }

        Arena a = getArena(id);

        a.getPlayers().add(player.getUniqueId());
        player.getInventory().clear();

        player.teleport(getSpawnLocation(a));

        if(!inven.contains("inv." + player.getUniqueId().toString())
                || inven.getString("inv." + player.getUniqueId().toString()) == null){
            Inventory defaultInventory = InventoryUtil.stringToInventory(inven.getString("default"));
            player.getInventory().setContents(defaultInventory.getContents());
        }
        else{
            Inventory playerInventory = InventoryUtil.stringToInventory(inven.getString("inv." + player.getUniqueId().toString()));
            player.getInventory().setContents(playerInventory.getContents());
        }

        player.setHealth(20);
        player.setFoodLevel(20);

        for(Player p: getInGamePlayers()){
            msgr.sendMessage(PrefixType.INFO, player.getDisplayName() + " has join the arena!", p);
        }

    }

    public List<Player> getInGamePlayers(){
        ArrayList<Player> players = new ArrayList<>();
        for(Player p: Bukkit.getOnlinePlayers()){
            if(isInGame(p)){
                players.add(p);
            }
        }

        return players;
    }

    private Location getSpawnLocation(Arena a){
        Location spawn = new Location(a.getMax().getWorld(), 0,0,0);
        int x,y,z;
        y = 0;
        x =  (int) (Math.random() * (a.getMax().getBlockX() - 5 + Math.abs(a.getMin().getBlockX() + 5)))
                + a.getMin().getBlockX() + 5;
        z =  (int) (Math.random() * (a.getMax().getBlockZ() - 5 + Math.abs(a.getMin().getBlockZ() + 5)))
                + a.getMin().getBlockZ() + 5;

        y = spawn.getWorld().getHighestBlockYAt(x, z);
        Block b = spawn.getWorld().getBlockAt(x, (y - 1), z);


        if(b.getType().compareTo(Material.LEAVES_2) == 0 ||
                b.getType().compareTo(Material.LEAVES) == 0){
            return getSpawnLocation(a);
        }
        spawn.setX(x);
        spawn.setZ(z);
        spawn.setY(y);
        return spawn;
    }

    //remove player from all arenas
    public boolean removePlayer(Player player){

        SettingsManager sm = SettingsManager.getManager();
        if(!isInGame(player)){
            return false;
        }

        for(Arena a: arenas){
            if(a.getPlayers().contains(player.getUniqueId())){
                a.getPlayers().remove(player.getUniqueId());
            }
        }

        Location lobby = LobbyManager.getManager().getLobbySpawn();

        if(lobby == null){
            msgr.sendMessage( PrefixType.WARNING, "There was no lobby to teleport you to! " +
                    "Please tell server admin to setup a lobby!", player);
        }
        else{
            player.teleport(LobbyManager.getManager().getLobbySpawn());
        }

        player.setHealth(20);
        player.setFoodLevel(20);
        return true;

    }

    //return true if the player is currently in an arena
    public boolean isInGame(Player p){

        for( Arena a: arenas){
            if(a.getPlayers().contains(p.getUniqueId())){
                return true;
            }
        }
        return false;
    }

    public String serializeLoc(Location loc){
        return "" + loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    public Location deSerializeLoc(String s){
        String[] s1 = s.split(",");
        try{
            return new Location(Bukkit.getWorld(s1[0]), Double.parseDouble(s1[1]), Double.parseDouble(s1[2]), Double.parseDouble(s1[3]));
        }
        catch (Exception e){
            Bukkit.getServer().getLogger().severe("Unable to deserialize Location");
            return null;
        }
    }

}
