package me.tycoondev.ninjacraft;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Chase on 6/16/2016.
 */
public class ShopListener implements Listener {

    public ShopListener(){

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
                    try {
                        int arenaNum = Integer.parseInt(identifiers[2].replace(" Shop", ""));
                        ArenaManager.getManager().addPlayer(e.getPlayer(), arenaNum);
                    } catch (Exception ex) {
                        //if worst comes to worse, they just use a command :P
                    }
                }
            }
        }
    }
}
