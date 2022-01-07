package me.cheesybones.chestlock;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands(){
        getServer().getPluginCommand("lockchest").setExecutor(new LockChestCommand());
        getServer().getPluginCommand("addmember").setExecutor(new AddMembersCommand());
    }

    private void registerListeners(){
        getServer().getPluginManager().registerEvents(new ChestBreak(),this);
    }
}
