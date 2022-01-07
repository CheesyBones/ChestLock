package me.cheesybones.chestlock;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.BlockIterator;

public class BlockHelper {
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
        NamespacedKey ownerKey = new NamespacedKey(Main.getPlugin(Main.class),"owner");
        NamespacedKey membersKey = new NamespacedKey(Main.getPlugin(Main.class),"members");

        container.remove(ownerKey);
        container.remove(membersKey);

        tileState.update();
    }
}
