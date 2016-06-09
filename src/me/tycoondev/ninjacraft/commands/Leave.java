package me.tycoondev.ninjacraft.commands;

import me.tycoondev.ninjacraft.*;
import me.tycoondev.ninjacraft.util.InventoryUtil;
import org.bukkit.entity.Player;

/**
 * Created by Chase on 5/28/2016.
 */
public class Leave implements SubCommand{

    @Override
    public void onCommand(Player player, String[] args) {
        if(!GamePlay.isTagged(player)) {
            Boolean leave = ArenaManager.getManager().removePlayer(player);
            if (!leave) {
                MessageManager.getManager().sendMessage(PrefixType.ERROR, "You are not in any arena!", player);
            } else {
                SettingsManager sm = SettingsManager.getManager();
                sm.getInvConfig().set("inv." + player.getUniqueId().toString(),
                        InventoryUtil.inventoryToString(player.getInventory()));
                sm.saveInvConfig();
                player.getInventory().clear();
            }
        }
        else{
            MessageManager.getManager().sendMessage(PrefixType.ERROR, "You may not leave while tagged in pvp!", player);
        }
    }

    @Override
    public String help(Player p) {
        return null;
    }

    @Override
    public String permission() {
        return null;
    }
}
