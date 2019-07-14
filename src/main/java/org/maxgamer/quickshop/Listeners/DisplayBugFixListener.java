package org.maxgamer.quickshop.Listeners;

import java.util.Collection;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Shop.DisplayItem;
import org.maxgamer.quickshop.Shop.DisplayType;
import org.maxgamer.quickshop.Util.Util;

public class DisplayBugFixListener implements Listener {
    private QuickShop plugin;

    public DisplayBugFixListener(QuickShop plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void canBuild(BlockCanBuildEvent e) {
        if (!plugin.isDisplay())
            return;
        if (DisplayItem.getNowUsing() != DisplayType.ARMORSTAND)
            return;
        if (e.isBuildable())
            return;
        Collection<Entity> entities = e.getBlock().getWorld().getNearbyEntities(e.getBlock().getLocation(), 1.0, 1, 1.0);
        for (Entity entity : entities) {
            Util.debugLog("Found entity: " + entity.getType().name() + " at " + entity.getLocation());
            if (!(entity instanceof ArmorStand))
                continue;
            ArmorStand armorStand = (ArmorStand) entity;
            if (DisplayItem.checkIsGuardItemStack(armorStand.getItemInHand())) {
                e.setBuildable(true);
                Util.debugLog("Re-set the allowed build flag here because it found the cause of the display-item blocking it before.");
                break;
            }
        }
    }
}
