package me.tycoondev.ninjacraft.commands;

import me.tycoondev.ninjacraft.ArenaManager;
import me.tycoondev.ninjacraft.MessageManager;
import me.tycoondev.ninjacraft.PrefixType;
import org.bukkit.entity.Player;

/**
 * Created by Chase on 6/6/2016.
 */
public class RemoveArena implements SubCommand {
    @Override
    public void onCommand(Player player, String[] args) {
        if(ArenaManager.getManager().removeArena(Integer.valueOf(args[0]))){
            MessageManager.getManager().sendMessage(PrefixType.INFO, "Arena removed successfully!", player);
        }
        else{
            MessageManager.getManager().sendMessage(PrefixType.WARNING, "Arena was unable to be removed!", player);
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
