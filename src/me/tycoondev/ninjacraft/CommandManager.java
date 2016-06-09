package me.tycoondev.ninjacraft;

import me.tycoondev.ninjacraft.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Chase on 5/25/2016.
 */
public class CommandManager implements CommandExecutor {
    private MessageManager msgr;
    private Plugin p;
    private HashMap<String, SubCommand> commands;
    public CommandManager(Plugin p){
        this.p = p;
        msgr = MessageManager.getManager();
        commands = new HashMap<String, SubCommand>();
        loadCommands();
    }

    private void loadCommands(){

        commands.put("createarena", new CreateArena());
        commands.put("join", new Join());
        commands.put("leave", new Leave());
        commands.put("setlobbyspawn", new SetLobbySpawn());
        commands.put("removearena", new RemoveArena());
        commands.put("setbalance", new SetBalance());
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)){
            Bukkit.getServer().getLogger().warning(ChatColor.RED + "Only players may execute ninjacraft commands!");
            return true;
        }

        Player player = (Player) sender;

        if(command.getName().equalsIgnoreCase("ninjacraft")){
            if(args == null || args.length < 1){
                //display debug info
                return true;
            }

            if(args[0].equalsIgnoreCase("help")){
                //display help

                return true;
            }

            //save first argument as subCommand, remove it from the array, and save the rest of the
            //arguments as arguments for the sub command.
            String subCommand = args[0];
            Vector<String> newArgs = new Vector<String>();
            newArgs.addAll(Arrays.asList(args));
            newArgs.remove(0);
            String[] tempargs = newArgs.toArray(new String[0]);

            if(!commands.containsKey(subCommand)){
                msgr.sendMessage(PrefixType.ERROR, "Command does not exist!", player);
                return true;
            }

            try {
                commands.get(subCommand).onCommand(player, tempargs);
            }
            catch (Exception e){
                e.printStackTrace();
                Bukkit.getServer().getLogger().warning("Error in the onCommand of NinjaCraft2");
            }
            return true;


        }

        return false;
    }
}
