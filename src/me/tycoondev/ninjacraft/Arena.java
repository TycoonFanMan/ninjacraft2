package me.tycoondev.ninjacraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Chase on 5/24/2016.
 */
public class Arena {

    private int ID;
    private Location max,min;
    private ArrayList<UUID> players;

    public Arena(int ID, Location max, Location min){
        this.ID = ID;
        this.max = max;
        this.min = min;
        players = new ArrayList<UUID>();
    }

    //Return the areas ID
    public int getID(){
        return ID;
    }

    //Check to see if a location is contained in the Arena
    public boolean contains(Location loc){
        if(loc.getWorld().getName().equals(max.getWorld().getName())
                && loc.getBlockX() > min.getBlockX() && loc.getBlockX() < max.getBlockX() + 1
                && loc.getBlockY() > min.getBlockY() && loc.getBlockY() < max.getBlockY() + 1
                && loc.getBlockZ() > min.getBlockZ() && loc.getBlockZ() < max.getBlockZ() + 1){
            return true;
        }
        return false;
    }

    public Location getMax(){
        return max;
    }

    public Location getMin(){
        return min;
    }

    //return the ArrayList of players currently in this arena
    public ArrayList<UUID> getPlayers(){
        return players;
    }



}
