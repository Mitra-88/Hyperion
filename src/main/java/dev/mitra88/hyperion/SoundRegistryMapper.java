package dev.mitra88.hyperion;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class SoundRegistryMapper {

    private static final Map<String, Sound> EXACT = new HashMap<>();
    private static final Map<String, Sound> ALIAS = new HashMap<>();
    private static boolean loaded = false;

    private SoundRegistryMapper() {}

    public static Sound get(String input, Sound fallback) {
        if (input == null || input.isBlank()) return fallback;

        String trimmed = input.trim();
        if (trimmed.indexOf('-') != -1) return fallback;
        if (!trimmed.matches("[A-Za-z0-9._:]+")) return fallback;

        ensureLoaded();

        String lower = trimmed.toLowerCase(Locale.ROOT);

        Sound exact = EXACT.get(lower);
        if (exact != null) return exact;

        Sound alias = ALIAS.get(normalize(lower));
        return alias != null ? alias : fallback;
    }

    private static synchronized void ensureLoaded() {
        if (loaded) return;

        Registry<Sound> registry = Registry.SOUNDS;
        for (Sound sound : registry) {
            NamespacedKey key = registry.getKey(sound);
            if (key == null) continue;

            putExact(key.toString(), sound);
            putExact(key.getKey(), sound);

            putAlias(key.toString(), sound);
            putAlias(key.getKey(), sound);
        }
        loaded = true;
    }

    private static void putExact(String alias, Sound sound) {
        if (alias == null || alias.isBlank()) return;
        EXACT.putIfAbsent(alias.toLowerCase(Locale.ROOT), sound);
    }

    private static void putAlias(String alias, Sound sound) {
        String normalized = normalize(alias);
        if (!normalized.isEmpty()) ALIAS.putIfAbsent(normalized, sound);
    }

    private static String normalize(String input) {
        return input.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[.:_]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }
}
