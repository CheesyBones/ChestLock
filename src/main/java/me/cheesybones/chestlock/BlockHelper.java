package me.cheesybones.chestlock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockHelper {
    static NamespacedKey ownerKey = new NamespacedKey(Main.getPlugin(Main.class),"owner");
    static NamespacedKey membersKey = new NamespacedKey(Main.getPlugin(Main.class),"members");

    public static Block getTargetBlock(Player player){
        BlockIterator iter = new BlockIterator(player,10);
        Block lastBlock = iter.next();
        while(iter.hasNext()){
            lastBlock = iter.next();
            if(lastBlock.getType() == Material.AIR){
                continue;
            }
            break;
        }

        return lastBlock;
    }

    public static void clearChestKeys(PersistentDataContainer container, TileState tileState){
        container.remove(ownerKey);
        container.remove(membersKey);

        tileState.update();
    }

    public static boolean checkChestOwnership(Block block, Player player){
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer container = tileState.getPersistentDataContainer();
        if (container.has(ownerKey, PersistentDataType.STRING)) {
            String owner = container.get(ownerKey,PersistentDataType.STRING);
            if(owner.equalsIgnoreCase(player.getUniqueId().toString())){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public static boolean checkChestAccess(Block block,Player player){
        TileState tileState = (TileState) block.getState();
        PersistentDataContainer container = tileState.getPersistentDataContainer();
        if(container.has(ownerKey,PersistentDataType.STRING)){
            String owner = container.get(ownerKey,PersistentDataType.STRING);
            if(owner.equalsIgnoreCase(player.getUniqueId().toString())){
                return true;
            }else{
                if(container.has(membersKey,PersistentDataType.STRING)){
                    String membersString = container.get(membersKey,PersistentDataType.STRING);
                    List<String> membersList = new ArrayList<String>(Arrays.asList(membersString.split(",")));
                    if(membersList.contains(player.getUniqueId().toString())){
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }
}
