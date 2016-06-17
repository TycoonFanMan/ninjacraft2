package me.tycoondev.ninjacraft;


import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.tycoondev.ninjacraft.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by Chase on 5/24/2016.
 */
public class NinjaCraft extends JavaPlugin implements Listener {

    GamePlay game;

    @Override
    public void onEnable() {
        /*try {
            MySQLManager.getManager().setup(this);
        } catch (SQLException e) {
            Bukkit.getServer().getLogger().severe("Could not Connect to MYSQL database!");
            e.printStackTrace();
        }*/

        SettingsManager.getManager().setup(this);
        MessageManager.getManager().setup(this);
        ArenaManager.getManager().setup(this);
        LobbyManager.getManager().setup(this);

        getCommand("ninjacraft").setExecutor(new CommandManager(this));

        game = new GamePlay(this);

        getServer().getPluginManager().registerEvents(game, this);
        getServer().getPluginManager().registerEvents(new ShopListener(), this);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                game.updateScoreboard();
            }
        }, 0, 5 * 20);
    }

    @Override
    public void onDisable() {
        /*try {
            MySQLManager.getManager().closeDB();
        } catch (SQLException e) {
            Bukkit.getServer().getLogger().severe("Could not close the MYSQL database!");
            e.printStackTrace();
        }*/
        game.respawnChests();

        for (Player p : ArenaManager.getManager().getInGamePlayers()) {
            if(ArenaManager.getManager().removePlayer(p)){
                SettingsManager sm = SettingsManager.getManager();
                sm.getInvConfig().set("inv." + p.getUniqueId().toString(),
                        InventoryUtil.inventoryToString(p.getInventory()));
                sm.saveInvConfig();
                p.getInventory().clear();
            }
        }

    }

    public static WorldEditPlugin getWorldEdit() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (!(p instanceof WorldEditPlugin)) {
            return null;
        }

        return (WorldEditPlugin) p;

    }

}
