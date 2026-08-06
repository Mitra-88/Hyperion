package dev.mitra88.hyperion;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class HyperionBuilder {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private static Component mm(String input) {
        return MM.deserialize(input).decoration(TextDecoration.ITALIC, false);
    }

    public static ItemStack build(HyperionConfig config) {
        ItemStack sword = new ItemStack(config.material);
        ItemMeta meta = sword.getItemMeta();
        if (meta == null) return sword;

        meta.displayName(mm(config.displayName));

        if (!config.lore.isEmpty()) {
            List<Component> lore = new ArrayList<>(config.lore.size());
            for (String line : config.lore) {
                lore.add(line.isEmpty() ? Component.empty() : mm(line));
            }
            meta.lore(lore);
        }

        if (!config.enchantments.isEmpty()) {
            Registry<Enchantment> registry = RegistryAccess.registryAccess()
                    .getRegistry(RegistryKey.ENCHANTMENT);
            for (Map.Entry<NamespacedKey, Integer> entry : config.enchantments.entrySet()) {
                Enchantment ench = registry.get(entry.getKey());
                if (ench != null) {
                    meta.addEnchant(ench, entry.getValue(), true);
                }
            }
        }

        meta.setUnbreakable(config.unbreakable);
        meta.getPersistentDataContainer().set(Hyperion.HYPERION_KEY, PersistentDataType.BYTE, (byte) 1);
        sword.setItemMeta(meta);

        if (!config.hiddenTooltipComponents.isEmpty()) {
            sword.setData(DataComponentTypes.TOOLTIP_DISPLAY,
                    TooltipDisplay.tooltipDisplay()
                            .addHiddenComponents(config.hiddenTooltipComponents.toArray(new DataComponentType[0]))
                            .build());
        }

        return sword;
    }

    public static boolean isHyperion(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(Hyperion.HYPERION_KEY, PersistentDataType.BYTE);
    }
}
