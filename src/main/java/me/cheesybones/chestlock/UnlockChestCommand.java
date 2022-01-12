package me.cheesybones.chestlock;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static me.cheesybones.chestlock.BlockHelper.getTargetBlock;

public class UnlockChestCommand implements CommandExecutor {
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

        boolean success;
        Chest chest = (Chest) targetBlock.getState();
        InventoryHolder holder = chest.getInventory().getHolder();
        if(holder instanceof DoubleChest){
            DoubleChest doubleChest = ((DoubleChest) holder);
            Chest rightChest = (Chest) doubleChest.getRightSide();
            Chest leftChest = (Chest) doubleChest.getLeftSide();
            unlockChest((TileState) rightChest.getBlock().getState(),player);
            success = unlockChest((TileState) leftChest.getBlock().getState(),player);

        }else{
            TileState tileState = (TileState) targetBlock.getState();

            success = unlockChest(tileState,player);
        }
        if(success){
            player.sendMessage(ChatColor.GOLD + "Chest successfully unlocked!");
            return true;
        }else{
            player.sendMessage(ChatColor.RED + "This chest cannot be unlocked");
            return false;
        }


    }

    private boolean unlockChest(TileState tileState,Player player){
        PersistentDataContainer container = tileState.getPersistentDataContainer();

        if(container.has(ownerKey, PersistentDataType.STRING)) {
            String uuid = container.get(ownerKey, PersistentDataType.STRING);
            if (uuid.equalsIgnoreCase(player.getUniqueId().toString())) {
                BlockHelper.clearChestKeys(container, tileState);
                return true;
            } else {
                return false;
            }
        }else{
            return false;
        }
    }
}
