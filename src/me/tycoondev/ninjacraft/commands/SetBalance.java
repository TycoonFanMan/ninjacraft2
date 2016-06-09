package me.tycoondev.ninjacraft.commands;

import me.tycoondev.ninjacraft.MessageManager;
import me.tycoondev.ninjacraft.PrefixType;
import me.tycoondev.ninjacraft.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Chase on 6/6/2016.
 */
public class SetBalance implements SubCommand {

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length != 2){
            return;
        }
        SettingsManager sm = SettingsManager.getManager();
        MessageManager msgr = MessageManager.getManager();

        try {
            Player p = Bukkit.getPlayer(args[0]);
            if(p == null) throw new Exception("Player not found");
            sm.getInvConfig().set("money." + p.getUniqueId().toString(), Integer.valueOf(args[1]));
            sm.saveInvConfig();
            msgr.sendMessage(PrefixType.INFO, args[0] + "'s Balance has been set to" + ChatColor.GOLD + " "
                    + Integer.valueOf(args[1]), player);
        } catch (Exception e){
            msgr.sendMessage(PrefixType.WARNING, "Failed to set player balance, check that the name is spelled properly" +
                    " and6 the desired balance is an integer.", player);
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
