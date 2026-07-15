package dev.mitra88.hyperion;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class HyperionCommand implements BasicCommand {

    @Override
    public void execute(@NonNull CommandSourceStack source, String @NonNull [] args) {
        if (!(source.getSender() instanceof Player player)) {
            return;
        }

        ItemStack aspectOfTheVoid = HyperionBuilder.giveHyperion();
        player.getInventory().addItem(aspectOfTheVoid);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>You have received the Hyperion."));
    }
}
