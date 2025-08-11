package dev.mitra88.hyperion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("ALL")
public class HyperionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        ItemStack hyperionSword = HyperionBuilder.giveHyperion();
        player.getInventory().addItem(hyperionSword);
        player.sendMessage(ColorUtils.color("&aYou have received the Hyperion"));

        return true;
    }
}
