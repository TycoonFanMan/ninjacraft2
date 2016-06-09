package me.tycoondev.ninjacraft.commands;

import org.bukkit.entity.Player;

/**
 * Created by Chase on 5/25/2016.
 */
public interface SubCommand {

    public void onCommand(Player player, String[] args);

    public String help(Player p);

    public String permission();
}
