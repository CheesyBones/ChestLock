package me.cheesybones.chestlock;

import org.bukkit.Bukkit;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.cheesybones.chestlock.BlockHelper.*;
import static me.cheesybones.chestlock.BlockHelper.checkChestOwnership;

public class RemoveMemberCommand implements CommandExecutor {
    NamespacedKey ownerKey = new NamespacedKey(Main.getPlugin(Main.class),"owner");
    NamespacedKey membersKey = new NamespacedKey(Main.getPlugin(Main.class),"members");
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

        String uuid;
        try{
            uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
        }catch(Exception exception){
            Bukkit.getLogger().info("Error in getting playername from uuid " + exception.toString());
            player.sendMessage(ChatColor.RED + "Error in getting player");
            return false;
        }

        Chest chest = (Chest) targetBlock.getState();
        InventoryHolder holder = chest.getInventory().getHolder();
        boolean success;
        if(holder instanceof DoubleChest){
            DoubleChest doubleChest = ((DoubleChest) holder);
            Chest rightChest = (Chest) doubleChest.getRightSide();
            Chest leftChest = (Chest) doubleChest.getLeftSide();

            if(!checkChestOwnership(leftChest.getBlock(),player) || !checkChestOwnership(rightChest.getBlock(),player)){
                success = false;
            }else{
                removeMember((TileState) rightChest.getBlock().getState(),uuid,player);
                success = removeMember((TileState) leftChest.getBlock().getState(),uuid,player);
            }


        }else{
            TileState tileState = (TileState) targetBlock.getState();
            success = removeMember(tileState,uuid,player);
        }

        if(success){
            player.sendMessage(ChatColor.DARK_AQUA + "Removed " + ChatColor.GOLD + args[0] + ChatColor.DARK_AQUA + " from chest");
            return true;
        }else{
            player.sendMessage(ChatColor.RED + "You cannot remove " + ChatColor.GOLD + args[0] + ChatColor.DARK_AQUA + " to chest");
            return false;
        }

    }

    private boolean removeMember(TileState tileState, String uuid,Player player){
        PersistentDataContainer container = tileState.getPersistentDataContainer();

        if(!container.has(ownerKey, PersistentDataType.STRING)) {
            return false;
        }

        if(!container.get(ownerKey,PersistentDataType.STRING).equalsIgnoreCase(player.getUniqueId().toString())){
            return false;
        }

        if(!container.has(membersKey,PersistentDataType.STRING)){
            return false;
        }

        String membersString = container.get(membersKey,PersistentDataType.STRING);
        List<String> membersList = new ArrayList<String>(Arrays.asList(membersString.split(",")));

        if(!membersList.contains(uuid)){
            return false;
        }

        membersList.remove(new String(uuid));
        String newMemberString = String.join(",",membersList);
        container.set(membersKey,PersistentDataType.STRING,newMemberString);

        tileState.update();

        return true;
    }
}
