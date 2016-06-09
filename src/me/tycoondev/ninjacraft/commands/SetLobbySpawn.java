package me.tycoondev.ninjacraft.commands;

import me.tycoondev.ninjacraft.LobbyManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Chase on 6/2/2016.
 */
public class SetLobbySpawn implements SubCommand {
    @Override
    public void onCommand(Player player, String[] args) {
        LobbyManager.getManager().setLobbySpawn(player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Lobby spawn set!");
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
