package me.tycoondev.ninjacraft.util;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Painting;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Chase on 5/28/2016.
 */
public class InventoryUtil {

    public static String prepareInventoryForDatabase(Inventory inv){

        MemoryConfiguration temp = new MemoryConfiguration();

        ItemStack[] items = inv.getContents();
        int count = 0;
        for(ItemStack i: items) {
            temp.createSection("item." + count, i.serialize());
            count++;
        }





        return temp.getConfigurationSection("item").toString();

    }

    public static String inventoryToString(Inventory inv){
        String serialization = inv.getSize() + ";";
        for(int i = 0; i < inv.getSize(); i++){
            ItemStack item = inv.getItem(i);
            if(item != null){
                String serializedItem = new String();

                String itemType = item.getType().toString();
                serializedItem += "t@" + itemType;

                if( item.getDurability() != 0){
                    String itemDura = String.valueOf(item.getDurability());
                    serializedItem += ":d@" + itemDura;
                }

                if( item.getAmount() != 1){
                    String itemAmount = String.valueOf(item.getAmount());
                    serializedItem += ":a@" + itemAmount;
                }

                if( item.hasItemMeta()) {
                    String itemName = String.valueOf(item.getItemMeta().getDisplayName());

                    if (!itemName.equals("null")) serializedItem += ":n@" + itemName;

                    String itemLore = String.valueOf(item.getItemMeta().getLore());
                    if (!itemLore.equals("null")) {
                        serializedItem += ":l@" + itemLore;
                    }

                    /*
                    This next part checks to see if the ItemStack contains additional item meta.
                    This code will be executed if the item is a leather armour piece or a potion.
                     */
                    if(item.getItemMeta() instanceof PotionMeta){
                        String itemEffect = ((PotionMeta) item.getItemMeta()).getBasePotionData().getType().toString();
                        boolean itemEffectUp = ((PotionMeta) item.getItemMeta()).getBasePotionData().isUpgraded();
                        boolean itemEffectExtended = ((PotionMeta) item.getItemMeta()).getBasePotionData().isExtended();

                        serializedItem += ":p@" + itemEffect + "@" + itemEffectUp + "@" + itemEffectExtended;
                    }

                    if(item.getItemMeta() instanceof LeatherArmorMeta){
                        String itemColor = String.valueOf(((LeatherArmorMeta) item.getItemMeta()).getColor().asRGB());
                        serializedItem += ":c@" + itemColor;
                    }

                }

                Map<Enchantment, Integer> itemEnch = item.getEnchantments();
                if(itemEnch.size() > 0){
                    for(Map.Entry<Enchantment, Integer> ench: itemEnch.entrySet()){
                        serializedItem += ":e@" + ench.getKey().getName() + "@" + ench.getValue();
                    }
                }
                serialization += i + "#" + serializedItem + ";";

            }
        }
        return serialization;
    }

    public static Inventory stringToInventory(String invString){
        String[] blocks = invString.split(";");
        String invInfo = blocks[0];
        Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);

        for(int i = 1; i < blocks.length; i++){
            String[] block = blocks[i].split("#");
            int slotNum = Integer.valueOf(block[0]);

            if (slotNum >= inv.getSize()){
                continue;
            }

            ItemStack item = null;
            Boolean createdItemStack = false;

            String[] serializedItem = block[1].split(":");
            for(String itemInfo: serializedItem){
                String[] itemAttribute = itemInfo.split("@");
                if(itemAttribute[0].equals("t")){
                    item = new ItemStack(Material.getMaterial(itemAttribute[1]));
                    createdItemStack = true;
                }
                else if(itemAttribute[0].equals("d") && createdItemStack){
                    item.setDurability(Short.valueOf(itemAttribute[1]));
                }
                else if(itemAttribute[0].equals("a") && createdItemStack){
                    item.setAmount(Integer.valueOf(itemAttribute[1]));
                }
                else if(itemAttribute[0].equals("n") && createdItemStack){
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(itemAttribute[1]);
                    item.setItemMeta(meta);
                }
                else if(itemAttribute[0].equals("l") && createdItemStack){
                    ItemMeta meta = item.getItemMeta();
                    String removeBrackets = itemAttribute[1].substring(1, itemAttribute[1].length() - 1);
                    ArrayList<String> lore = new ArrayList<>();
                    for(String loreSegment: removeBrackets.split(", ")){
                        lore.add(loreSegment);
                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
                else if(itemAttribute[0].equals("p") && createdItemStack){
                    PotionMeta meta = (PotionMeta) item.getItemMeta();
                    PotionData data = new PotionData(PotionType.valueOf(itemAttribute[1]), Boolean.valueOf(itemAttribute[3]), Boolean.valueOf(itemAttribute[2]));

                    meta.setBasePotionData(data);
                    item.setItemMeta(meta);
                }
                else  if(itemAttribute[0].equals("c") && createdItemStack){
                    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                    meta.setColor(Color.fromRGB(Integer.valueOf(itemAttribute[1])));

                    item.setItemMeta(meta);
                }
                else if(itemAttribute[0].equals("e") && createdItemStack){
                    item.addEnchantment(Enchantment.getByName(String.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
                }
            }
            inv.setItem(slotNum, item);
        }
        return inv;
    }
}
