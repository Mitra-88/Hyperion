package dev.mitra88.hyperion;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class HyperionBuilder {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private static Component mm(String input) {
        return MM.deserialize(input).decoration(TextDecoration.ITALIC, false);
    }

    public static ItemStack giveHyperion() {
        ItemStack hyperionSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = hyperionSword.getItemMeta();
        if (meta == null) return hyperionSword;

        meta.displayName(mm("<light_purple>Shiny Heroic Hyperion <gold>✪✪✪✪<red>➎"));

        List<Component> lore = List.of(
                mm("<gray>Gear Score: <light_purple>1315 <dark_gray>(5000)"),
                mm("<gray>Damage: <red>366 <yellow>(+30) <dark_gray>(+2,125)"),
                mm("<gray>Strength: <red>250 <yellow>(+30) <gold>[+5] <blue>(+50) <dark_gray>(+1,468.75)"),
                mm("<gray>Crit Damage: <red>+70% <dark_gray>(+437.5%)"),
                mm("<gray>Bonus Attack Speed: <red>+7% <blue>(+7%) <dark_gray>(+10.5%)"),
                mm("<gray>Intelligence: <green>+634 <blue>(+125) <light_purple>(+24) <dark_gray>(+3,743.75)"),
                mm("<gray>Ferocity: <green>+33 <dark_gray>(+45)"),
                mm("<dark_purple>[<aqua>✎<dark_purple>] <dark_purple>[<aqua>⚔<dark_purple>]"),
                Component.empty(),
                mm("<light_purple><bold>Ultimate Wise V<blue>, <blue>Champion X, Cleave VI"),
                mm("<blue>Critical VII, Cubism VI, Divine Gift III"),
                mm("<blue>Dragon Hunter V, Ender Slayer VII, Execute VI"),
                mm("<blue>Experience V, Fire Aspect III, First Strike IV"),
                mm("<blue>Giant Killer VII, Impaling III, Lethality VI"),
                mm("<blue>Looting V, Luck VII, Mana Steal III"),
                mm("<blue>Scavenger V, Smite VII, Smoldering V"),
                mm("<blue>Tabasco III, Thunderlord VII, Vampirism VI"),
                mm("<blue>Venomous VI"),
                Component.empty(),
                mm("<aqua>Music Rune III</aqua>"),
                Component.empty(),
                mm("<gray>Deals +<red>50% <gray>damage to Withers."),
                mm("<gray>Grants <red>+1 ❁ Damage <gray>and <green>+2 <aqua>✎"),
                mm("<aqua>Intelligence <gray>per <red>Catacombs <gray>level."),
                Component.empty(),
                mm("<green>Scroll Abilities:"),
                mm("<gold>Ability: Wither Impact <yellow><bold>RIGHT CLICK"),
                mm("<gray>Teleport <green>10 blocks<gray> ahead of you."),
                mm("<gray>Then implode dealing <red>145,720.2 <gray>damage"),
                mm("<gray>to nearby enemies. Also applies the"),
                mm("<gray>wither shield scroll ability reducing"),
                mm("<gray>damage taken and granting an"),
                mm("<gray>absorption shield for <yellow>5 <gray>seconds."),
                mm("<dark_gray>Mana Cost: <dark_aqua>150"),
                Component.empty(),
                mm("<light_purple><bold><obfuscated>A</obfuscated> SHINY MYTHIC DUNGEON SWORD <obfuscated>A</obfuscated>")
        );
        meta.lore(lore);

        Enchantment unbreaking = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)
                .get(NamespacedKey.minecraft("unbreaking"));
        if (unbreaking != null) {
            meta.addEnchant(unbreaking, 100, true);
        }

        meta.setUnbreakable(true);
        meta.getPersistentDataContainer().set(Hyperion.HYPERION_KEY, PersistentDataType.BYTE, (byte) 1);
        hyperionSword.setItemMeta(meta);

        //noinspection UnstableApiUsage
        hyperionSword.setData(DataComponentTypes.TOOLTIP_DISPLAY,
                TooltipDisplay.tooltipDisplay()
                        .addHiddenComponents(
                                DataComponentTypes.UNBREAKABLE,
                                DataComponentTypes.ENCHANTMENTS,
                                DataComponentTypes.STORED_ENCHANTMENTS,
                                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                                DataComponentTypes.TRIM,
                                DataComponentTypes.DYED_COLOR
                        )
                        .build());

        return hyperionSword;
    }

    public static boolean isHyperion(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(Hyperion.HYPERION_KEY, PersistentDataType.BYTE);
    }
}
