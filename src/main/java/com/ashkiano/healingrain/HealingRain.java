package com.ashkiano.healingrain;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealingRain extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new RainListener(this), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("gethealingrain") && sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("healingrain.give")) {
                player.getInventory().addItem(RainListener.getHealingRainItem());
                player.sendMessage("§aYou've received the Healing Rain item!");
                return true;
            } else {
                player.sendMessage("§cYou don't have permission to get this item.");
            }
        }
        return false;
    }
}