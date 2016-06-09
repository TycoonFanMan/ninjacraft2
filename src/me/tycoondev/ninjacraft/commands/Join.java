package me.tycoondev.ninjacraft.commands;

import me.tycoondev.ninjacraft.ArenaManager;
import org.bukkit.entity.Player;

/**
 * Created by Chase on 5/26/2016.
 */
public class Join implements SubCommand {
    @Override
    public void onCommand(Player player, String[] args){
        ArenaManager.getManager().addPlayer(player, Integer.valueOf(args[0]));
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
