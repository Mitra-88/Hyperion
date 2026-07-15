package dev.mitra88.hyperion;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public final class HyperionCommand implements BasicCommand {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final HyperionConfig config;
    private final boolean reload;

    public HyperionCommand(HyperionConfig config, boolean reload) {
        this.config = config;
        this.reload = reload;
    }

    @Override
    public void execute(@NonNull CommandSourceStack source, String @NonNull [] args) {
        CommandSender sender = source.getSender();

        if (reload) {
            if (!sender.hasPermission("hyperion.reload")) {
                sender.sendMessage(MM.deserialize("<red>You do not have permission to use this command."));
                return;
            }
            config.reload();
            sender.sendMessage(MM.deserialize("<green>Hyperion config reloaded successfully."));
            return;
        }

        if (!sender.hasPermission("hyperion.give")) {
            sender.sendMessage(MM.deserialize("<red>You do not have permission to use this command."));
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MM.deserialize("<red>This command can only be run by a player."));
            return;
        }

        ItemStack sword = HyperionBuilder.build(config);
        player.getInventory().addItem(sword).forEach((_, leftover) ->
                player.getWorld().dropItem(player.getLocation(), leftover));
        player.sendMessage(MM.deserialize("<green>You have received the Hyperion."));
    }
}