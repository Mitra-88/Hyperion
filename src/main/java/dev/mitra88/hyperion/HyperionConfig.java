package dev.mitra88.hyperion;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public final class HyperionConfig {

    // Teleport
    public double teleportMaxDistance;
    public double teleportStep;
    public Sound teleportSound;
    public float teleportSoundVolume;
    public float teleportSoundPitch;

    // Damage (Implosion)
    public double damageMin;
    public double damageMax;
    public double damageRadius;

    // Healing (Wither Shield)
    public long healingCooldownMs;
    public PotionEffectType resistanceEffect;
    public int resistanceDuration;
    public int resistanceAmplifier;
    public PotionEffectType absorptionEffect;
    public int absorptionDuration;
    public int absorptionAmplifier;
    public PotionEffectType instantHealthEffect;
    public int instantHealthDuration;
    public int instantHealthAmplifier;
    public Sound healingSound;
    public float healingSoundVolume;
    public float healingSoundPitch;

    // Item
    public Material material;
    public String displayName;
    public List<String> lore;
    public boolean unbreakable;
    public Map<NamespacedKey, Integer> enchantments;
    public Set<DataComponentType> hiddenTooltipComponents;

    private final JavaPlugin plugin;

    public HyperionConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();

        this.teleportMaxDistance   = cfg.getDouble("teleport.max-distance", 10.0);
        this.teleportStep          = cfg.getDouble("teleport.step", 0.5);
        this.teleportSound         = loadSound(cfg.getString("teleport.sound", "ENTITY_GENERIC_EXPLODE"));
        this.teleportSoundVolume   = (float) cfg.getDouble("teleport.sound-volume", 1.0);
        this.teleportSoundPitch    = (float) cfg.getDouble("teleport.sound-pitch", 1.0);

        this.damageMin   = cfg.getDouble("damage.min", 20_000.0);
        this.damageMax   = cfg.getDouble("damage.max", 50_000.0);
        this.damageRadius = cfg.getDouble("damage.radius", 6.0);

        this.healingCooldownMs       = cfg.getLong("healing.cooldown-ms", 5_000L);
        this.resistanceEffect        = loadPotionEffect(cfg.getString("healing.resistance-effect", "resistance"));
        this.resistanceDuration      = cfg.getInt("healing.resistance-duration", 100);
        this.resistanceAmplifier     = cfg.getInt("healing.resistance-amplifier", 5);
        this.absorptionEffect        = loadPotionEffect(cfg.getString("healing.absorption-effect", "absorption"));
        this.absorptionDuration      = cfg.getInt("healing.absorption-duration", 100);
        this.absorptionAmplifier     = cfg.getInt("healing.absorption-amplifier", 5);
        this.instantHealthEffect     = loadPotionEffect(cfg.getString("healing.instant-health-effect", "instant_health"));
        this.instantHealthDuration   = cfg.getInt("healing.instant-health-duration", 1);
        this.instantHealthAmplifier  = cfg.getInt("healing.instant-health-amplifier", 10);
        this.healingSound            = loadSound(cfg.getString("healing.sound", "ENTITY_ZOMBIE_VILLAGER_CURE"));
        this.healingSoundVolume      = (float) cfg.getDouble("healing.sound-volume", 1.0);
        this.healingSoundPitch       = (float) cfg.getDouble("healing.sound-pitch", 1.0);

        Material mat = Material.matchMaterial(cfg.getString("item.material", "IRON_SWORD"));
        this.material = (mat != null) ? mat : Material.IRON_SWORD;
        this.displayName = cfg.getString("item.display-name",
                "<light_purple>Shiny Heroic Hyperion <gold>✪✪✪✪<red>➎");
        this.lore = Collections.unmodifiableList(cfg.getStringList("item.lore"));
        this.unbreakable = cfg.getBoolean("item.unbreakable", true);
        this.enchantments = loadEnchantments(cfg.getConfigurationSection("item.enchantments"));
        this.hiddenTooltipComponents = loadHiddenComponents(cfg.getStringList("item.tooltip-hidden-components"));
    }

    private static PotionEffectType loadPotionEffect(String name) {
        if (name == null || name.isBlank()) return null;
        NamespacedKey key = parseKey(name);
        if (key == null) return null;
        return Registry.POTION_EFFECT_TYPE.get(key);
    }

    private static Sound loadSound(String name) {
        if (name == null || name.isBlank()) return Sound.ENTITY_GENERIC_EXPLODE;
        NamespacedKey key = parseKey(name);
        if (key == null) return Sound.ENTITY_GENERIC_EXPLODE;
        Sound sound = Registry.SOUNDS.get(key);
        return sound != null ? sound : Sound.ENTITY_GENERIC_EXPLODE;
    }

    private static Map<NamespacedKey, Integer> loadEnchantments(ConfigurationSection section) {
        Map<NamespacedKey, Integer> ench = new LinkedHashMap<>();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                int level = section.getInt(key);
                if (level < 1) continue;
                NamespacedKey nk = parseKey(key);
                if (nk != null) ench.put(nk, level);
            }
        }
        if (ench.isEmpty()) ench.put(NamespacedKey.minecraft("unbreaking"), 100);
        return Collections.unmodifiableMap(ench);
    }

    private static Set<DataComponentType> loadHiddenComponents(List<String> raw) {
        Set<DataComponentType> components = new HashSet<>();
        Registry<DataComponentType> registry = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.DATA_COMPONENT_TYPE);

        for (String s : raw) {
            if (s == null || s.isBlank()) continue;
            NamespacedKey key = parseKey(s);
            if (key == null) continue;
            DataComponentType type = registry.get(key);
            if (type != null) components.add(type);
        }

        if (components.isEmpty()) {
            components.add(DataComponentTypes.UNBREAKABLE);
            components.add(DataComponentTypes.ENCHANTMENTS);
            components.add(DataComponentTypes.STORED_ENCHANTMENTS);
            components.add(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            components.add(DataComponentTypes.TRIM);
            components.add(DataComponentTypes.DYED_COLOR);
        }
        return Collections.unmodifiableSet(components);
    }

    private static NamespacedKey parseKey(String input) {
        String trimmed = input.trim().toLowerCase(Locale.ROOT);
        if (trimmed.isEmpty()) return null;
        return trimmed.indexOf(':') >= 0
                ? NamespacedKey.fromString(trimmed)
                : NamespacedKey.minecraft(trimmed);
    }
}
