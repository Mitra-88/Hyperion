package dev.mitra88.hyperion;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HyperionBuilder {
    public static ItemStack giveHyperion() {
        ItemStack hyperionSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = hyperionSword.getItemMeta();

        Objects.requireNonNull(meta).setDisplayName(ColorUtils.color("&dShiny Heroic Hyperion &6✪✪✪✪&c➎"));

        List<String> lore = Arrays.asList(
                ColorUtils.color("&7Gear Score: &d1184 &8(5000)"),
                ColorUtils.color("&7Damage: &c366 &e(+30) &8(+2,125)"),
                ColorUtils.color("&7Strength: &c250 &e(+30) &6[+5] &9(+50) &8(+1,468.75)"),
                ColorUtils.color("&7Crit Damage: &c+70% &8(+437.5%)"),
                ColorUtils.color("&7Bonus Attack Speed: &c+7% &9(+7%) &8(+10.5%)"),
                ColorUtils.color("&7Intelligence: &a+634 &9(+125) &d(+24) &8(+3,743.75)"),
                ColorUtils.color("&7Ferocity: &a+33 &8(+45)"),
                ColorUtils.color("&5[&b✎&5] &5[&b⚔&5]"),
                "",
                ColorUtils.color("&d&lUltimate Wise V&9, &9Champion X, Cleave VI"),
                ColorUtils.color("&9Critical VII, Cubism VI, Divine Gift III"),
                ColorUtils.color("&9Dragon Hunter V, Ender Slayer VII, Execute VI"),
                ColorUtils.color("&9Experience V, Fire Aspect III, First Strike IV"),
                ColorUtils.color("&9Giant Killer VII, Impaling III, Lethality VI"),
                ColorUtils.color("&9Looting V, Luck VII, Mana Steal III"),
                ColorUtils.color("&9Scavenger V, Smite VII, Smoldering V"),
                ColorUtils.color("&9Tabasco III, Thunderlord VII, Vampirism VI"),
                ColorUtils.color("&9Venomous VI"),
                "",
                ColorUtils.color("&5◆ Music Rune III"),
                "",
                ColorUtils.color("&7Deals +&c50% &7damage to Withers."),
                ColorUtils.color("&7Grants &c+1 ❁ Damage &7and &a+2 &b✎"),
                ColorUtils.color("&bIntelligence &7per &cCatacombs &7level."),
                "",
                ColorUtils.color("&aScroll Abilities:"),
                ColorUtils.color("&6Ability: Wither Impact &e&lRIGHT CLICK"),
                ColorUtils.color("&7Teleport &a10 blocks&7 ahead of you."),
                ColorUtils.color("&7Then implode dealing &c36,647 &7damage"),
                ColorUtils.color("&7to nearby enemies. Also applies the"),
                ColorUtils.color("&7wither shield scroll ability reducing"),
                ColorUtils.color("&7damage taken and granting an"),
                ColorUtils.color("&7absorption shield for &e5 &7seconds."),
                ColorUtils.color("&8Mana Cost: &3150"),
                "",
                ColorUtils.color("&d&l&kA&a &d&lSHINY MYTHIC DUNGEON SWORD &kA")
        );

        meta.setLore(lore);
        meta.addEnchant(Enchantment.UNBREAKING, 100, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        hyperionSword.setItemMeta(meta);

        return hyperionSword;
    }

    public static boolean isHyperion(ItemStack item) {
        if (item == null || item.getType() != Material.IRON_SWORD) {
            return false;
        }

        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (!Objects.requireNonNull(meta).hasDisplayName()) {
            return false;
        }

        String expectedDisplayName = ColorUtils.color("&dShiny Heroic Hyperion &6✪✪✪✪&c➎");

        return meta.getDisplayName().equals(expectedDisplayName);
    }
}
