package me.tycoondev.ninjacraft;

import me.tycoondev.ninjacraft.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Chase on 6/5/2016.
 */
public class GamePlay implements Listener, Runnable {

    private HashMap<UUID, Scoreboard> boards;
    private static HashMap<UUID, Integer> pvptagged;
    private static final int TAG_TIME = 20; //in seconds;

    private Plugin p;

    private ScoreboardManager sbm;
    private SettingsManager sm;
    private MessageManager msgr;

    public GamePlay( Plugin p ) {
        this.p = p;
        sbm = Bukkit.getServer().getScoreboardManager();
        sm = SettingsManager.getManager();
        msgr = MessageManager.getManager();
        boards = new HashMap<>();
        pvptagged = new HashMap<>();
    }

    public static boolean isTagged(Player p){
        return pvptagged.containsKey(p.getUniqueId());
    }

    @Override
    public void run(){
        updateScoreboard();
    }

    public void updateScoreboard(){
        for(Player online: Bukkit.getOnlinePlayers()){
            if (!(boards.containsKey(online.getUniqueId()))) {
                boards.put(online.getUniqueId(), sbm.getNewScoreboard());
            }

            Scoreboard board = boards.get(online.getUniqueId());
            Objective o = board.getObjective(DisplaySlot.SIDEBAR);
            if(o != null) o.unregister();
            o = board.registerNewObjective("ninjacraft", "dummy");
            o.setDisplayName(ChatColor.BLACK + "[" + ChatColor.RED + "NinjaCraft"
                    + ChatColor.BLACK + "]");
            o.setDisplaySlot(DisplaySlot.SIDEBAR);

            if(!sm.getInvConfig().contains("money." + online.getUniqueId().toString())){
                sm.getInvConfig().set("money." + online.getUniqueId().toString(), 0);
                sm.saveInvConfig();
            }

            o.getScore("").setScore(2);
            o.getScore(ChatColor.GOLD + "Money: " + ChatColor.GREEN
                    + sm.getInvConfig().getInt("money." + online.getUniqueId().toString())).setScore(1);

            online.setScoreboard(board);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventArenaGreif(PlayerInteractEvent e) {
        if(e.isCancelled()) return;
        if (ArenaManager.getManager().isInGame(e.getPlayer())) {
            if(e.getClickedBlock().getType().isBlock()) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventArenaPlace(BlockPlaceEvent e) {
        if (ArenaManager.getManager().isInGame(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void signJoin(PlayerInteractEvent e) {
        if (!(e.getAction().compareTo(Action.RIGHT_CLICK_BLOCK) == 0)) return;

        if (e.getClickedBlock().getType().compareTo(Material.SIGN) == 0
                || e.getClickedBlock().getType().compareTo(Material.SIGN_POST) == 0
                || e.getClickedBlock().getType().compareTo(Material.WALL_SIGN) == 0) {

            Sign sign = (Sign) e.getClickedBlock().getState();
            String[] identifiers = sign.getLines();


            if (identifiers[0].equalsIgnoreCase("[ninjacraft]")) {
                e.setCancelled(true);

                //Join Sign
                if (identifiers[1].equalsIgnoreCase("Click to Join")) {
                    try {
                        int arenaNum = Integer.parseInt(identifiers[2].replace("Arena ", ""));
                        ArenaManager.getManager().addPlayer(e.getPlayer(), arenaNum);
                    } catch (Exception ex) {
                        //if worst comes to worse, they just use a command :P
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if(ArenaManager.getManager().isInGame(p) && e.getDamage() >= p.getHealth()){
            e.setCancelled(true);
            Location drops = p.getLocation();
            ArenaManager.getManager().removePlayer(p);

            sm.getInvConfig().set("money." + p.getUniqueId().toString(), 0);
            sm.getInvConfig().set("inv." + p.getUniqueId().toString(), null);
            sm.saveInvConfig();

            for(ItemStack item: p.getInventory()){
                try {
                    p.getWorld().dropItemNaturally(drops, item);
                } catch (Exception ex){
                    //Throws an exception in a Null inventory spot, but continues just fine!
                }
            }

            p.getInventory().clear();

            MessageManager.getManager().broadcastMessage(PrefixType.INFO, p.getDisplayName()
                    + ChatColor.RED + " died! ");
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e){
        Player player = e.getPlayer();
        if(!isTagged(player)) {
            Boolean leave = ArenaManager.getManager().removePlayer(player);


            SettingsManager sm = SettingsManager.getManager();
            sm.getInvConfig().set("inv." + player.getUniqueId().toString(),
                    InventoryUtil.inventoryToString(player.getInventory()));
            sm.saveInvConfig();
            player.getInventory().clear();
        }
        else{
            Location drops = player.getLocation();
            ArenaManager.getManager().removePlayer(player);

            sm.getInvConfig().set("money." + player.getUniqueId().toString(), 0);
            sm.getInvConfig().set("inv." + player.getUniqueId().toString(), null);
            sm.saveInvConfig();

            for(ItemStack item: player.getInventory()){
                try {
                    player.getWorld().dropItemNaturally(drops, item);
                } catch (Exception ex){
                    //Throws an exception in a Null inventory spot, but continues just fine!
                }
            }

            player.getInventory().clear();
        }


    }

    @EventHandler
    public void onKill(EntityDamageByEntityEvent e){
        if(!(e.getDamager() instanceof Player && e.getEntity() instanceof Player)){
            return;
        }
        Player dmg = (Player) e.getDamager();
        Player hit = (Player) e.getEntity();

        if(e.getDamage() >= hit.getHealth()){
            e.setCancelled(true);
            Location drops = hit.getLocation();
            ArenaManager.getManager().removePlayer(hit);
            //hit.teleport(LobbyManager.getManager().getLobbySpawn());

            sm.getInvConfig().set("money." + dmg.getUniqueId().toString(),
                    (sm.getInvConfig().getInt("money." + dmg.getUniqueId().toString()) +
                            sm.getInvConfig().getInt("money." + hit.getUniqueId().toString())));

            sm.getInvConfig().set("money." + hit.getUniqueId().toString(), 0);
            sm.getInvConfig().set("inv." + hit.getUniqueId().toString(), null);
            sm.saveInvConfig();

            for(ItemStack item: hit.getInventory()){
                try {
                    hit.getWorld().dropItemNaturally(drops, item);
                } catch (Exception ex){
                    //Same as above about the Null Item
                }
            }

            hit.getInventory().clear();

            String weapon = dmg.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
            if(weapon == null || weapon.equalsIgnoreCase("null")){
                weapon = dmg.getInventory().getItemInMainHand().getType().toString();
            }

            MessageManager.getManager().broadcastMessage(PrefixType.INFO, dmg.getDisplayName()
                    + ChatColor.RED + " killed " + ChatColor.GREEN + hit.getDisplayName()
                    + ChatColor.RED + " with " + ChatColor.GOLD + weapon);
        }
    }

    @EventHandler
    public void combatTag(EntityDamageByEntityEvent e){
        if(e.isCancelled()) return;
        if((e.getDamager() instanceof Player || e.getDamager() instanceof Projectile) && e.getEntity() instanceof Player){
            Player dmg;
            if(e.getDamager() instanceof Projectile){
                dmg = (Player) ((Projectile) e.getDamager()).getShooter();
            }
            else{
                dmg = (Player) e.getDamager();
            }



            //If player is already tagged, cancel the task and then restart it.
            if(pvptagged.containsKey(dmg.getUniqueId())){
                Bukkit.getServer().getScheduler().cancelTask(pvptagged.get(dmg.getUniqueId()));

            }
            else{
                MessageManager.getManager().sendMessage(PrefixType.INFO, "You have been Combat Tagged!", dmg);
                MessageManager.getManager().sendMessage(PrefixType.INFO, "While tagged you are unable to enter safe zones and command use is restricted.", dmg);
                MessageManager.getManager().sendMessage(PrefixType.INFO, "Stay out of combat for " + TAG_TIME + " seconds to become untagged.", dmg);
            }

            pvptagged.put(dmg.getUniqueId(), Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(p, new Runnable() {
                @Override
                public void run() {
                    pvptagged.remove(dmg.getUniqueId());
                    msgr.sendMessage(PrefixType.INFO, "You have left combat!", dmg);
                }
            }, TAG_TIME * 20));

        }
    }
}