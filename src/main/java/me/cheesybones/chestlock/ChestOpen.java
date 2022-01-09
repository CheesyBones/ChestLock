package me.cheesybones.chestlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import static me.cheesybones.chestlock.BlockHelper.checkChestMembers;
import static me.cheesybones.chestlock.BlockHelper.checkChestOwner;
import static me.cheesybones.chestlock.BlockHelper.checkChestAccess;

public class ChestOpen implements Listener {

    @EventHandler
    public void onOpen(PlayerInteractEvent event){
        if(!event.hasBlock()){
            return;
        }
        if(event.getClickedBlock().getType() != Material.CHEST){
            return;
        }
        if(!(event.getClickedBlock().getState() instanceof TileState)){
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
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
