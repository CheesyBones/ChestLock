package me.cheesybones.chestlock;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import static me.cheesybones.chestlock.BlockHelper.*;

public class ChestBreak implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(block.getType() != Material.CHEST) {return;}
        if(!(block.getState() instanceof TileState)){return;}

        TileState tileState = (TileState) block.getState();
        PersistentDataContainer container = tileState.getPersistentDataContainer();

        NamespacedKey ownerKey = new NamespacedKey(Main.getPlugin(Main.class),"owner");
        NamespacedKey membersKey = new NamespacedKey(Main.getPlugin(Main.class),"members");
        Chest chest = (Chest) block.getState();
        InventoryHolder holder = chest.getInventory().getHolder();
        if(holder instanceof DoubleChest){
            DoubleChest doubleChest = ((DoubleChest) holder);
            Chest leftChest = (Chest) doubleChest.getLeftSide();
            Chest rightChest = (Chest) doubleChest.getRightSide();
            if(!checkChestAccess(leftChest.getBlock(),player) || !checkChestAccess(rightChest.getBlock(),player)){
                player.sendMessage(ChatColor.RED + "You do not have access to this chest!");
                event.setCancelled(true);
            }
        }else{
            if(!checkChestAccess(block,player)){
                player.sendMessage(ChatColor.RED + "You do not have access to this chest!");
                event.setCancelled(true);
            }
        }
    }

}
