package me.tycoondev.ninjacraft;

import me.tycoondev.ninjacraft.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
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
import org.bukkit.event.inventory.InventoryOpenEvent;
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
public class GamePlay implements Listener {
    private final int MONEY_MAX = 50;
    private final int MONEY_MIN = 10;

    private HashMap<String, Shop> shops;

    private HashMap<UUID, Scoreboard> boards;
    private static HashMap<UUID, Integer> pvpTagged;
    private ArrayList<Block> lootedChests;
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
        lootedChests = new ArrayList<>();
        boards = new HashMap<>();
        pvpTagged = new HashMap<>();
        shops = new HashMap<>();
        loadShops();
    }

    private void loadShops(){
        for(String s: sm.getShopConfig().getConfigurationSection("shops").getKeys(false)){
            shops.put(s, new Shop(s));
        }
    }

    public static boolean isTagged(Player p){
        return pvpTagged.containsKey(p.getUniqueId());
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void preventArenaGreif(BlockBreakEvent e) {
        if(e.isCancelled()) return;
        if (ArenaManager.getManager().isInGame(e.getPlayer())) {
            if(ArenaManager.getManager().contains(e.getBlock().getLocation())){
                e.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void preventArenaPlace(BlockPlaceEvent e) {
        if (ArenaManager.getManager().isInGame(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
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
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onShopClick(PlayerInteractEvent e){
        if (!(e.getAction().compareTo(Action.RIGHT_CLICK_BLOCK) == 0)) return;

        if (e.getClickedBlock().getType().compareTo(Material.SIGN) == 0
                || e.getClickedBlock().getType().compareTo(Material.SIGN_POST) == 0
                || e.getClickedBlock().getType().compareTo(Material.WALL_SIGN) == 0) {

            Sign sign = (Sign) e.getClickedBlock().getState();
            String[] identifiers = sign.getLines();


            if (identifiers[0].equalsIgnoreCase("[ninjacraft]")) {
                e.setCancelled(true);

                //Join Sign
                if (identifiers[1].equalsIgnoreCase("Click to open")) {
                    String name = (identifiers[2].replace(" Shop", ""));
                    Shop shop = shops.get(name);
                    if (shop != null) {
                        shop.openShop(e.getPlayer());
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
                if(((Projectile) e.getDamager()).getShooter() instanceof Player) {
                    dmg = (Player) ((Projectile) e.getDamager()).getShooter();
                }
                else{
                    return;
                }
            }
            else{
                dmg = (Player) e.getDamager();
            }



            //If player is already tagged, cancel the task and then restart it.
            if(pvpTagged.containsKey(dmg.getUniqueId())){
                Bukkit.getServer().getScheduler().cancelTask(pvpTagged.get(dmg.getUniqueId()));

            }
            else{
                MessageManager.getManager().sendMessage(PrefixType.INFO, "You have been Combat Tagged!", dmg);
                MessageManager.getManager().sendMessage(PrefixType.INFO, "While tagged you are unable to enter safe zones and command use is restricted.", dmg);
                MessageManager.getManager().sendMessage(PrefixType.INFO, "Stay out of combat for " + TAG_TIME + " seconds to become untagged.", dmg);
            }

            pvpTagged.put(dmg.getUniqueId(), Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(p, new Runnable() {
                @Override
                public void run() {
                    pvpTagged.remove(dmg.getUniqueId());
                    msgr.sendMessage(PrefixType.INFO, "You have left combat!", dmg);
                }
            }, TAG_TIME * 20));

        }
    }

    private void giveMoneyLoot(Player player){
        Integer ranMon = randomMoney();
        sm.getInvConfig().set("money." + player.getUniqueId().toString(),
                (sm.getInvConfig().getInt("money." + player.getUniqueId().toString()) + ranMon));
        sm.saveInvConfig();

        msgr.sendMessage(PrefixType.INFO, "You found " + ChatColor.GOLD + "$" +  ranMon + ChatColor.GREEN + " in the chest!", player);
    }

    private Integer randomMoney(){
        int money = (int) (Math.random() * (MONEY_MAX - MONEY_MIN)) + MONEY_MIN;
        return money;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onChestLoot(InventoryOpenEvent e){
        if(e.getInventory().getHolder() instanceof Chest || e.getInventory().getHolder() instanceof DoubleChest){

            Location chestLoc = ((Chest) e.getInventory().getHolder()).getLocation();
            if(ArenaManager.getManager().contains(chestLoc)){
                e.setCancelled(true);
                giveMoneyLoot((Player) e.getPlayer());

                //drop rare items <- TO BE IMPLEMENTED AFTER A BASIC RUNNABLE BUILD IS COMPLETED

                Block chest = chestLoc.getWorld().getBlockAt(chestLoc);

                //remove the chest
                chest.setType(Material.AIR);
                lootedChests.add(chest);

                //respawn the chest in 45-120 seconds
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(p, new Runnable() {
                    @Override
                    public void run() {
                        chest.setType(Material.CHEST);
                        lootedChests.remove(chest);
                    }
                }, ((int) Math.random() * (120 - 45) + 45) * 20);
            }
        }
    }

    public void respawnChests(){
        for(Block b: lootedChests){
            b.setType(Material.CHEST);
            lootedChests.remove(b);
        }
    }
}
