package com.ashkiano.healingrain;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RainListener implements Listener {

    private HashMap<UUID, Long> cooldowns = new HashMap<>();
    private HealingRain plugin;

    public RainListener(HealingRain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if ("Healing Rain".equals(meta.getDisplayName()) && meta.getLore() != null && meta.getLore().contains("§7Special item that heals nearby players.")) {

                if (cooldowns.containsKey(player.getUniqueId())) {
                    long cooldownTime = cooldowns.get(player.getUniqueId());
                    long timeLeft = (5 * 60 * 1000) - (System.currentTimeMillis() - cooldownTime);

                    if (timeLeft > 0) {
                        long minutesLeft = timeLeft / (60 * 1000);
                        long secondsLeft = (timeLeft % (60 * 1000)) / 1000;
                        player.sendMessage(String.format("§cYou need to wait %d minutes and %d seconds for the cooldown!", minutesLeft, secondsLeft));
                        return;
                    }
                }


                cooldowns.put(player.getUniqueId(), System.currentTimeMillis());

                Location loc = player.getLocation();
                createHealingRain(loc);
            }
        }
    }


    private void createHealingRain(Location center) {
        int radius = 5;
        for (int degree = 0; degree < 360; degree += 10) {
            double radian = Math.toRadians(degree);
            double x = center.getX() + (radius * Math.cos(radian));
            double z = center.getZ() + (radius * Math.sin(radian));
            Location particleLocation = new Location(center.getWorld(), x, center.getY(), z);

            center.getWorld().spawnParticle(Particle.HEART, particleLocation, 10, 0.5, 0.5, 0.5, 0);

            List<Player> nearbyPlayers = new ArrayList<>(Bukkit.getWorld(center.getWorld().getName()).getPlayers());
            nearbyPlayers.removeIf(p -> p.getLocation().distance(center) > radius);

            for (Player p : nearbyPlayers) {
                p.setHealth(Math.min(p.getHealth() + 1, p.getMaxHealth()));
            }
        }
    }

    public static ItemStack getHealingRainItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Healing Rain");

        List<String> lore = new ArrayList<>();
        lore.add("§7Special item that heals nearby players.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }
}