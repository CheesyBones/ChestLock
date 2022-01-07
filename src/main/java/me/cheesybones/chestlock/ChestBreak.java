package me.cheesybones.chestlock;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.cheesybones.chestlock.BlockHelper.clearChestKeys;

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
        if(container.has(ownerKey, PersistentDataType.STRING)){
            String owner = container.get(ownerKey,PersistentDataType.STRING);
            if(owner.equalsIgnoreCase(player.getUniqueId().toString())){
                clearChestKeys(container,tileState);
                return;
            }

            if(container.has(membersKey,PersistentDataType.STRING)){
                String membersString = container.get(membersKey,PersistentDataType.STRING);
                List<String> membersList = new ArrayList<String>(Arrays.asList(membersString.split(",")));
                if(membersList.contains(player.getUniqueId().toString())){
                    clearChestKeys(container,tileState);
                    return;
                }
            }
        }else{
            return;
        }
        event.setCancelled(true);

    }

}
