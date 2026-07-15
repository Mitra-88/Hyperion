package dev.mitra88.hyperion;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class HyperionEventListener implements Listener {

    private static final double TELEPORT_MAX_DISTANCE = 10.0;
    private static final double TELEPORT_STEP = 0.5;
    private static final int    TELEPORT_STEPS        = (int) (TELEPORT_MAX_DISTANCE / TELEPORT_STEP);

    private static final double DAMAGE_MIN       = 20_000.0;
    private static final double DAMAGE_MAX       = 50_000.0;
    private static final double DAMAGE_RADIUS    = 6.0;
    private static final double DAMAGE_RADIUS_SQ = DAMAGE_RADIUS * DAMAGE_RADIUS;

    private static final long HEALING_COOLDOWN_MS = 5_000L;

    private static final PotionEffectType RESISTANCE =
            Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft("resistance"));
    private static final PotionEffectType ABSORPTION =
            Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft("absorption"));
    private static final PotionEffectType INSTANT_HEALTH =
            Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft("instant_health"));

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final Object2LongOpenHashMap<UUID> healingCooldowns = new Object2LongOpenHashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!HyperionBuilder.isHyperion(item)) return;

        teleportPlayer(player);
        damageNearbyEntities(player);

        long now = System.currentTimeMillis();
        if (healingCooldowns.getOrDefault(player.getUniqueId(), 0L) <= now) {
            applyHealingEffects(player);
            healingCooldowns.put(player.getUniqueId(), now + HEALING_COOLDOWN_MS);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (isHoldingHyperion(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        healingCooldowns.removeLong(event.getPlayer().getUniqueId());
    }

    private boolean isHoldingHyperion(Player player) {
        var inv = player.getInventory();
        return HyperionBuilder.isHyperion(inv.getItemInMainHand())
                || HyperionBuilder.isHyperion(inv.getItemInOffHand());
    }

    private void teleportPlayer(Player player) {
        Location start = player.getLocation();
        Vector step = start.getDirection().multiply(TELEPORT_STEP);

        Location cursor = start.clone();
        for (int i = 0; i < TELEPORT_STEPS; i++) {
            Location next = cursor.clone().add(step);
            if (!next.getBlock().isPassable()) break;
            cursor = next;
        }

        player.teleport(cursor, PlayerTeleportEvent.TeleportCause.PLUGIN);

        var world = player.getWorld();
        world.playSound(cursor, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        world.spawnParticle(Particle.EXPLOSION, cursor, 5);
    }

    private void damageNearbyEntities(Player player) {
        double damage = DAMAGE_MIN + Math.random() * (DAMAGE_MAX - DAMAGE_MIN);

        Location origin = player.getLocation();
        double ox = origin.getX();
        double oy = origin.getY();
        double oz = origin.getZ();

        int hitCount = 0;
        for (Entity entity : player.getNearbyEntities(DAMAGE_RADIUS, DAMAGE_RADIUS, DAMAGE_RADIUS)) {
            if (!(entity instanceof LivingEntity le) || entity instanceof Player) continue;

            double dx = entity.getX() - ox;
            double dy = entity.getY() - oy;
            double dz = entity.getZ() - oz;
            if (dx * dx + dy * dy + dz * dz > DAMAGE_RADIUS_SQ) continue;

            le.damage(damage, player);
            hitCount++;
        }

        if (hitCount == 0) return;
        player.sendMessage(MM.deserialize(String.format(
                "<gray>Your Implosion hit <red>%d <gray>enemies for <red>%.2f <gray>damage.",
                hitCount, hitCount * damage)));
    }

    @SuppressWarnings("DataFlowIssue")
    private void applyHealingEffects(Player player) {
        player.addPotionEffect(new PotionEffect(RESISTANCE, 100, 5));
        player.addPotionEffect(new PotionEffect(ABSORPTION, 100, 5));
        player.addPotionEffect(new PotionEffect(INSTANT_HEALTH, 1, 10));

        Location loc = player.getLocation();
        var world = player.getWorld();
        world.playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0f, 1.0f);
        world.spawnParticle(Particle.EXPLOSION, loc, 1);
    }
}
