package me.cheesybones.chestlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BlockIterator;

import static me.cheesybones.chestlock.BlockHelper.getTargetBlock;

public class LockChestCommand implements CommandExecutor {

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

        TileState tileState = (TileState) targetBlock.getState();

        PersistentDataContainer container = tileState.getPersistentDataContainer();

        NamespacedKey ownerKey = new NamespacedKey(Main.getPlugin(Main.class),"owner");

        if(container.has(ownerKey,PersistentDataType.STRING)){
            String uuid = container.get(ownerKey,PersistentDataType.STRING);
            if(uuid.equalsIgnoreCase(player.getUniqueId().toString())){
                BlockHelper.clearChestKeys(container,tileState);
                player.sendMessage(ChatColor.GOLD + "Chest successfully unlocked!");
            }else{
                player.sendMessage(ChatColor.RED + "This chest is already locked by another player!");
            }
            return false;

        }else{
            if(args.length == 0){
                container.set(ownerKey, PersistentDataType.STRING,player.getUniqueId().toString());
            }else{
                player.sendMessage("Chest locked under " + args[0]);
                container.set(ownerKey,PersistentDataType.STRING,args[0]);
            }
            tileState.update();
            player.sendMessage(ChatColor.DARK_AQUA + "Chest successfully locked!");
        }

        return true;
    }
}
