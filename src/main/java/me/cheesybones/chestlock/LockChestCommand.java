package me.cheesybones.chestlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static me.cheesybones.chestlock.BlockHelper.checkChestAccess;
import static me.cheesybones.chestlock.BlockHelper.getTargetBlock;

public class LockChestCommand implements CommandExecutor {
    NamespacedKey ownerKey = new NamespacedKey(Main.getPlugin(Main.class),"owner");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return false;
        }

        Player player = (Player) sender;
        Block targetBlock = getTargetBlock(player);
        if(targetBlock.getType() != Material.CHEST){
            player.sendMessage(ChatColor.RED + "Can only use command on Material Type CHEST!");
            return false;
        }
        if(!(targetBlock.getState() instanceof TileState)){
            return false;
        }

        Chest chest = (Chest) targetBlock.getState();
        InventoryHolder holder = chest.getInventory().getHolder();
        boolean success;
        if(holder instanceof DoubleChest){
            DoubleChest doubleChest = ((DoubleChest) holder);
            Chest rightChest = (Chest) doubleChest.getRightSide();
            Chest leftChest = (Chest) doubleChest.getLeftSide();
            if(!checkChestAccess(leftChest.getBlock(),player) || !checkChestAccess(rightChest.getBlock(),player)){
                success = false;
            }else {
                lockChest((TileState) rightChest.getBlock().getState(), player, args);
                success = lockChest((TileState) leftChest.getBlock().getState(), player, args);
            }
        }else{
            TileState tileState = (TileState) targetBlock.getState();
            success = lockChest(tileState,player,args);
        }

        if(success){
            player.sendMessage(ChatColor.DARK_AQUA + "Chest successfully locked");
            return true;
        }else{
            player.sendMessage(ChatColor.RED + "You cannot lock this chest");
            return false;
        }

    }

    private boolean lockChest(TileState tileState,Player player,String[] args){
        PersistentDataContainer container = tileState.getPersistentDataContainer();
        if(container.has(ownerKey, PersistentDataType.STRING)){
            return false;
        }
        String returnMsg;
        if(args.length == 0){ // this is a debug feature, remove in actual build
            container.set(ownerKey, PersistentDataType.STRING,player.getUniqueId().toString());

        }else{
            container.set(ownerKey,PersistentDataType.STRING,args[0]);
        }
        tileState.update();
        return true;
    }
}
