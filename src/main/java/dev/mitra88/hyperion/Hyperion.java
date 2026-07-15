package dev.mitra88.hyperion;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class Hyperion extends JavaPlugin {

    public static NamespacedKey HYPERION_KEY;

    @Override
    public void onEnable() {
        HYPERION_KEY = new NamespacedKey(this, "hyperion");

        getServer().getPluginManager().registerEvents(new HyperionEventListener(), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                    "givehyperion",
                    "Gives the Hyperion sword to the player.",
                    new HyperionCommand()
            );
        });
    }
}
