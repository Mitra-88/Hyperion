package dev.mitra88.hyperion;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.*;

public class HyperionEventListener implements Listener {
    private final Map<UUID, Long> healingCooldowns = new HashMap<>();
    private static final double TELEPORT_MAX_DISTANCE = 10.0;
    private static final double TELEPORT_STEP = 0.5;
    private static final double DAMAGE_MIN = 20000;
    private static final double DAMAGE_MAX = 50000;
    private static final double DAMAGE_RADIUS = 6.0;
    private static final long HEALING_COOLDOWN = 5000L;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!HyperionBuilder.isHyperion(item)) return;

        teleportPlayer(player);
        int entitiesDamaged = damageNearbyEntities(player);
        notifyPlayerOfDamage(player, entitiesDamaged);

        if (canUseHealing(player)) {
            applyHealingEffects(player);
            setHealingCooldown(player);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && isHoldingHyperion(player)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isHoldingHyperion(Player player) {
        return HyperionBuilder.isHyperion(player.getInventory().getItemInMainHand()) ||
                HyperionBuilder.isHyperion(player.getInventory().getItemInOffHand());
    }

    private void teleportPlayer(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        Location teleportDestination = findTeleportDestination(player, direction);
        player.teleport(teleportDestination);

        World world = player.getWorld();
        Location playerLocation = player.getLocation();
        world.playSound(playerLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        world.spawnParticle(Particle.EXPLOSION, playerLocation, 5);
    }

    private Location findTeleportDestination(Player player, Vector direction) {
        Location location = player.getLocation();
        for (double i = 0; i < TELEPORT_MAX_DISTANCE; i += TELEPORT_STEP) {
            Location testLocation = location.clone().add(direction.clone().multiply(i));
            if (!testLocation.getBlock().isPassable()) return location.clone().add(direction.clone().multiply(i - TELEPORT_STEP));
        }
        return location.clone().add(direction.clone().multiply(TELEPORT_MAX_DISTANCE));
    }

    private int damageNearbyEntities(Player player) {
        double damage = DAMAGE_MIN + (Math.random() * (DAMAGE_MAX - DAMAGE_MIN));
        int entitiesDamaged = 0;

        for (Entity entity : player.getNearbyEntities(DAMAGE_RADIUS, DAMAGE_RADIUS, DAMAGE_RADIUS)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                ((LivingEntity) entity).damage(damage);
                entitiesDamaged++;
            }
        }
        return entitiesDamaged;
    }

    private void notifyPlayerOfDamage(Player player, int entitiesDamaged) {
        if (entitiesDamaged > 0) {
            double totalDamage = entitiesDamaged * (DAMAGE_MIN + (Math.random() * (DAMAGE_MAX - DAMAGE_MIN)));
            String formattedDamage = String.format("%.2f", totalDamage);
            String message = String.format(
                    "<gray>Your Implosion hit <red>%d <gray>enemies for <red>%s <gray>damage.",
                    entitiesDamaged, formattedDamage
            );
            player.sendMessage(MM.deserialize(message));
        }
    }

    private boolean canUseHealing(Player player) {
        Long cooldownEnd = healingCooldowns.get(player.getUniqueId());
        return cooldownEnd == null || System.currentTimeMillis() >= cooldownEnd;
    }

    private void applyHealingEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 10));

        World world = player.getWorld();
        Location playerLocation = player.getLocation();
        world.playSound(playerLocation, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0f, 1.0f);
        world.spawnParticle(Particle.EXPLOSION, playerLocation, 1);
    }

    private void setHealingCooldown(Player player) {
        healingCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + HEALING_COOLDOWN);
        new BukkitRunnable() {
            @Override
            public void run() {
                healingCooldowns.remove(player.getUniqueId());
            }
        }.runTaskLater(JavaPlugin.getPlugin(Hyperion.class), HEALING_COOLDOWN / 50);
    }
}
