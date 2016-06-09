package me.tycoondev.ninjacraft.commands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.tycoondev.ninjacraft.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import me.tycoondev.ninjacraft.MessageManager;

/**
 * Created by Chase on 5/25/2016.
 */
public class CreateArena implements SubCommand{

    public CreateArena(){

    }

    @Override
    public void onCommand(Player player, String[] args) {
        MessageManager msgr = MessageManager.getManager();

        WorldEditPlugin worldEdit = NinjaCraft.getWorldEdit();

        if(worldEdit.getSelection(player) == null){
            msgr.sendMessage(PrefixType.ERROR, "You must make a world edit selection first!", player);
            return;
        }



        Location sel1 = worldEdit.getSelection(player).getMaximumPoint();
        Location sel2 = worldEdit.getSelection(player).getMinimumPoint();

        Location max = new Location(sel1.getWorld(), Math.max(sel1.getBlockX(), sel2.getBlockX()) , Math.max(sel1.getBlockY(), sel2.getBlockY()), Math.max(sel1.getBlockZ(), sel2.getBlockZ()));
        Location min = new Location(sel1.getWorld(), Math.min(sel1.getBlockX(), sel2.getBlockX()) , Math.min(sel1.getBlockY(), sel2.getBlockY()), Math.min(sel1.getBlockZ(), sel2.getBlockZ()));

        Arena a = ArenaManager.getManager().createArena(max, min);

        msgr.sendMessage(PrefixType.INFO, "Arena created successfully! This arena's ID: " + a.getID(), player);


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
