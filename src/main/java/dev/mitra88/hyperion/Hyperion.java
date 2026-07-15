package dev.mitra88.hyperion;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hyperion extends JavaPlugin {

    public static final NamespacedKey HYPERION_KEY = new NamespacedKey("hyperion", "hyperion");

    private HyperionConfig config;
    private HyperionEventListener listener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = new HyperionConfig(this);
        listener = new HyperionEventListener(config);
        getServer().getPluginManager().registerEvents(listener, this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();
            commands.register("givehyperion",
                    "Gives the Hyperion sword to the player.",
                    new HyperionCommand(config, false));
            commands.register("hyperionreload",
                    "Reloads the Hyperion configuration.",
                    new HyperionCommand(config, true));
        });
    }

    @Override
    public void onDisable() {
        if (listener != null) listener.cleanup();
    }
}
