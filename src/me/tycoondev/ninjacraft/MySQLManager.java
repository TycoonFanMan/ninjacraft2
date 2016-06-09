package me.tycoondev.ninjacraft;

import me.tycoondev.ninjacraft.huskehhh.mysql.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Chase on 5/27/2016.
 */
/*
public class MySQLManager {
    private NinjaCraft plugin;
    private MySQL db;
    private static MySQLManager manager = new MySQLManager();

    private MySQLManager(){

    }

    public static MySQLManager getManager(){
        return manager;
    }

    public void setup(NinjaCraft plugin) throws SQLException {
        this.plugin = plugin;

        db = new MySQL();
        try {
            db.openConnection();
        } catch (ClassNotFoundException e) {
            Bukkit.getServer().getLogger().warning("Error in setup of MySQLManager");
            e.printStackTrace();
        }

        Statement statement = db.getConnection().createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS 'ninjaInventories' ('player_id' VARCHAR(36) NOT NULL PRIMARY KEY," +
                "'slot1' VARCHAR(200)," +
                "'slot2' VARCHAR(200)," +
                "'slot3' VARCHAR(200)," +
                "'slot4' VARCHAR(200)," +
                "'slot5' VARCHAR(200)," +
                "'slot6' VARCHAR(200)," +
                "'slot7' VARCHAR(200)," +
                "'slot8' VARCHAR(200)," +
                "'slot9' VARCHAR(200)," +
                "'slot10' VARCHAR(200)," +
                "'slot11' VARCHAR(200)," +
                "'slot12' VARCHAR(200)," +
                "'slot13' VARCHAR(200)," +
                "'slot14' VARCHAR(200)," +
                "'slot15' VARCHAR(200)," +
                "'slot16' VARCHAR(200)," +
                "'slot17' VARCHAR(200)," +
                "'slot18' VARCHAR(200)," +
                "'slot19' VARCHAR(200)," +
                "'slot20' VARCHAR(200)," +
                "'slot21' VARCHAR(200)," +
                "'slot22' VARCHAR(200)," +
                "'slot23' VARCHAR(200)," +
                "'slot24' VARCHAR(200)," +
                "'slot25' VARCHAR(200)," +
                "'slot26' VARCHAR(200)," +
                "'slot27' VARCHAR(200)," +
                "'slot28' VARCHAR(200)," +
                "'slot29' VARCHAR(200)," +
                "'slot30' VARCHAR(200)," +
                "'slot31' VARCHAR(200)," +
                "'slot32' VARCHAR(200)," +
                "'slot33' VARCHAR(200)," +
                "'slot34' VARCHAR(200)," +
                "'slot35' VARCHAR(200)," +
                "'slot36' VARCHAR(200)" +
                "");
        statement.close();
    }

    public void closeDB() throws SQLException {
        db.closeConnection();
    }
}
*/