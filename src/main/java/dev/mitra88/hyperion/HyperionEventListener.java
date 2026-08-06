package dev.mitra88.hyperion;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Particle;
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
import org.bukkit.util.Vector;

import java.util.UUID;

public final class HyperionEventListener implements Listener {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final HyperionConfig config;
    private final Object2LongOpenHashMap<UUID> healingCooldowns = new Object2LongOpenHashMap<>();

    public HyperionEventListener(HyperionConfig config) {
        this.config = config;
    }

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
            healingCooldowns.put(player.getUniqueId(), now + config.healingCooldownMs);
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
        Vector step = start.getDirection().multiply(config.teleportStep);

        int steps = (int) (config.teleportMaxDistance / config.teleportStep);

        Location cursor = start.clone();
        for (int i = 0; i < steps; i++) {
            Location next = cursor.clone().add(step);
            if (!next.getBlock().isPassable()) break;
            cursor = next;
        }

        player.teleport(cursor, PlayerTeleportEvent.TeleportCause.PLUGIN);

        var world = player.getWorld();
        world.playSound(cursor, config.teleportSound, config.teleportSoundVolume, config.teleportSoundPitch);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, cursor, 1);
    }

    private void damageNearbyEntities(Player player) {
        double min = Math.min(config.damageMin, config.damageMax);
        double max = Math.max(config.damageMin, config.damageMax);
        double damage = (min >= max) ? min : min + Math.random() * (max - min);

        double radius = config.damageRadius;
        double radiusSq = radius * radius;

        Location origin = player.getLocation();
        double ox = origin.getX();
        double oy = origin.getY();
        double oz = origin.getZ();

        int hitCount = 0;
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (!(entity instanceof LivingEntity le) || entity instanceof Player) continue;

            double dx = entity.getX() - ox;
            double dy = entity.getY() - oy;
            double dz = entity.getZ() - oz;
            if (dx * dx + dy * dy + dz * dz > radiusSq) continue;

            le.damage(damage, player);
            hitCount++;
        }

        if (hitCount == 0) return;
        player.sendMessage(MM.deserialize(String.format(
                "<gray>Your Implosion hit <red>%d <gray>enemies for <red>%.2f <gray>damage.",
                hitCount, hitCount * damage)));
    }

    private void applyHealingEffects(Player player) {
        if (config.resistanceEffect != null) {
            player.addPotionEffect(new PotionEffect(
                    config.resistanceEffect, config.resistanceDuration, config.resistanceAmplifier));
        }
        if (config.absorptionEffect != null) {
            player.addPotionEffect(new PotionEffect(
                    config.absorptionEffect, config.absorptionDuration, config.absorptionAmplifier));
        }
        if (config.instantHealthEffect != null) {
            player.addPotionEffect(new PotionEffect(
                    config.instantHealthEffect, config.instantHealthDuration, config.instantHealthAmplifier));
        }

        Location loc = player.getLocation();
        var world = player.getWorld();
        world.playSound(loc, config.healingSound, config.healingSoundVolume, config.healingSoundPitch);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
    }

    public void cleanup() {
        healingCooldowns.clear();
    }
}
