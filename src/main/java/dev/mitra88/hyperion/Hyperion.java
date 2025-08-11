package dev.mitra88.hyperion;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Hyperion extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new HyperionEventListener(), this);
        Objects.requireNonNull(getCommand("givehyperion")).setExecutor(new HyperionCommand());
    }
}
