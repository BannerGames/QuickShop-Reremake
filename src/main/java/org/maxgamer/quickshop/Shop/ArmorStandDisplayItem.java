package org.maxgamer.quickshop.Shop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.*;
import org.maxgamer.quickshop.Event.ShopDisplayItemDespawnEvent;
import org.maxgamer.quickshop.Event.ShopDisplayItemSpawnEvent;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Util.Util;

public class ArmorStandDisplayItem implements DisplayItem {

    private QuickShop plugin = QuickShop.instance;
    private ItemStack originalItemStack;
    private ItemStack guardedIstack;
    private ArmorStand armorStand;
    private Shop shop;

    public ArmorStandDisplayItem(@NotNull Shop shop) {
        this.shop = shop;
        this.originalItemStack = shop.getItem().clone();
    }

    @Override
    public void spawn() {
        if (shop.getLocation().getWorld() == null) {
            Util.debugLog("Canceled the displayItem spawning because the location in the world is null.");
            return;
        }

        if (originalItemStack == null) {
            Util.debugLog("Canceled the displayItem spawning because the ItemStack is null.");
            return;
        }

        if (armorStand != null && armorStand.isValid() && !armorStand.isDead()) {
            Util.debugLog("Warning: Spawning the armorStand for DisplayItem when there is already an existing armorStand may cause a duplicated armorStand!");
            StackTraceElement[] traces = Thread.currentThread().getStackTrace();
            for (StackTraceElement trace : traces) {
                Util.debugLog(trace.getClassName() + "#" + trace.getMethodName() + "#" + trace.getLineNumber());
            }
        }
        ShopDisplayItemSpawnEvent shopDisplayItemSpawnEvent = new ShopDisplayItemSpawnEvent(shop, originalItemStack);
        Bukkit.getPluginManager().callEvent(shopDisplayItemSpawnEvent);
        if (shopDisplayItemSpawnEvent.isCancelled()) {
            Util.debugLog("Canceled the displayItem from spawning because a plugin setCancelled the spawning event, usually it is a QuickShop Add on");
            return;
        }
        this.armorStand = (ArmorStand) this.shop.getLocation().getWorld()
                .spawnEntity(getDisplayLocation(), EntityType.ARMOR_STAND);
        //Set basic armorstand datas.
        this.armorStand.setArms(false);
        this.armorStand.setBasePlate(false);
        this.armorStand.setVisible(false);
        this.armorStand.setGravity(false);
        this.armorStand.setSilent(true);
        this.armorStand.setAI(false);
        this.armorStand.setCollidable(false);
        this.armorStand.setCanPickupItems(false);
        //this.armorStand.setSmall(true);

        //Set armorstand item in hand
//        this.armorStand.setItemInHand(new ItemStack(originalItemStack.getType()));
        //Set safeGuard
        safeGuard(this.armorStand);
        //Set pose
        setPoseForArmorStand();
        Util.debugLog("Spawned a new ArmorStand DisplayItem for a shop " + shop.getLocation().toString());
    }

    @Override
    public void safeGuard(@NotNull Entity entity) {
        if (!(entity instanceof ArmorStand)) {
            Util.debugLog("Failed to safeGuard " + entity.getLocation().toString() + ", cause target not a ArmorStand");
            return;
        }
        ArmorStand armorStand = (ArmorStand) entity;
        //Set item protect in the armorstand's hand
        this.guardedIstack = DisplayItem.createGuardItemStack(this.originalItemStack);
        armorStand.setItemInHand(guardedIstack);
        Util.debugLog("Successfully safeGuard ArmorStand: " + armorStand.getLocation().toString());
    }

    private void setPoseForArmorStand() {
        if (this.originalItemStack.getType().isBlock()) {
            this.armorStand.setRightArmPose(new EulerAngle(-0.2, 0, 0));
        } else {
            this.armorStand.setRightArmPose(new EulerAngle(-89.5, 0, 0));
        }
    }

    @Override
    public void remove() {
        if (this.armorStand == null || !this.armorStand.isValid() || this.armorStand.isDead()) {
            Util.debugLog("Ignore the armorStand removing because the armorStand is already gone.");
            return;
        }
        this.armorStand.remove();
        this.armorStand = null;
        this.guardedIstack = null;
        ShopDisplayItemDespawnEvent shopDisplayItemDespawnEvent = new ShopDisplayItemDespawnEvent(this.shop, this.originalItemStack);
        Bukkit.getPluginManager().callEvent(shopDisplayItemDespawnEvent);
    }

    @Override
    public void respawn() {
        remove();
        spawn();
    }

    @Override
    public Entity getDisplay() {
        return this.armorStand;
    }

    @Override
    public Location getDisplayLocation() {
        BlockFace containerBlockFace = BlockFace.NORTH; //Set default vaule
        if (this.shop.getLocation().getBlock().getBlockData() instanceof Directional) {
            containerBlockFace = ((Directional) this.shop.getLocation().getBlock().getBlockData())
                    .getFacing(); //Replace by container face.
        }
        Location asloc = this.shop.getLocation().clone();
        Util.debugLog("containerBlockFace " + containerBlockFace);
        if (this.originalItemStack.getType().isBlock()) {
            asloc.add(0, 1, 0);
        }
        switch (containerBlockFace) {
            case SOUTH:
                if (isTool(this.originalItemStack.getType())) {
                    asloc.setYaw(90);
                    asloc.add(0.9, -0.4, 1);
                } else if (originalItemStack.getType().isBlock()) {
                    asloc.add(0.87, -0.4, 0.2);
                } else {
                    asloc.setYaw(0);
                    asloc.add(0.9, -0.4, 0);
                }
                break;
            case WEST:
                if (isTool(this.originalItemStack.getType())) {
                    asloc.add(0.9, -0.4, 0);
                } else if (originalItemStack.getType().isBlock()) {
                    asloc.add(0.85, -0.4, 0.15);
                } else {
                    asloc.setYaw(-90);
                    asloc.add(-0.1, -0.4, 0.15);
                }
                break;
            case EAST:
                if (isTool(this.originalItemStack.getType())) {
                    asloc.add(0.9, -0.4, 0);
                } else if (originalItemStack.getType().isBlock()) {
                    asloc.setYaw(-90);
                    asloc.add(0.15, -0.4, 0.1);
                } else {
                    asloc.setYaw(-90);
                    asloc.add(0, -0.4, 0.1);
                }
                break;
            case NORTH:
                if (isTool(this.originalItemStack.getType())) {
                    asloc.setYaw(90);
                    asloc.add(1, -0.4, 1);
                } else if (originalItemStack.getType().isBlock()) {
                    asloc.add(0.85, -0.4, 0.1);
                } else {
                    asloc.add(0.9, -0.4, 0);
                }
                break;
            default:
                break;
        }
        return asloc;
    }

    @Override
    public boolean checkDisplayIsMoved() {
        if (this.armorStand == null) {
            return false;
        }
        return !this.armorStand.getLocation().equals(getDisplayLocation());
    }

    @Override
    public boolean checkDisplayNeedRegen() {
        if (this.armorStand == null) {
            return false;
        }
        return !this.armorStand.isValid() || this.armorStand.isDead();
    }

    @Override
    public boolean checkIsShopEntity(@NotNull Entity entity) {
        if (!(entity instanceof ArmorStand)) {
            return false;
        }
        return DisplayItem.checkIsGuardItemStack(((ArmorStand) entity).getItemInHand());
    }

    @Override
    public boolean removeDupe() {
        if (this.armorStand == null) {
            Util.debugLog("Warning: Trying to removeDupe for a null display shop.");
            return false;
        }
        boolean removed = false;
        //Chunk chunk = shop.getLocation().getChunk();
        for (Entity entity : armorStand.getNearbyEntities(1, 1, 1)) {
            if (!(entity instanceof ArmorStand)) {
                continue;
            }
            ArmorStand eArmorStand = (ArmorStand) entity;
            if (plugin.getItemMatcher().matches(eArmorStand.getItemInHand(), this.guardedIstack)) {
                if (!eArmorStand.getUniqueId().equals(this.armorStand.getUniqueId())) {
                    Util.debugLog("Removing dupes ArmorEntity " + eArmorStand.getUniqueId().toString() + " at " + eArmorStand
                            .getLocation().toString());
                    entity.remove();
                    removed = true;
                }
            }
        }
        return removed;
    }

    @Override
    public void fixDisplayMoved() {
        for (Entity entity : this.shop.getLocation().getWorld().getEntities()) {
            if (!(entity instanceof ArmorStand)) {
                continue;
            }
            ArmorStand eArmorStand = (ArmorStand) entity;
            if (eArmorStand.getUniqueId().equals(this.armorStand.getUniqueId())) {
                Util.debugLog("Fixing moved ArmorStand displayItem " + eArmorStand.getUniqueId().toString() + " at " + eArmorStand
                        .getLocation().toString());
                eArmorStand.teleport(getDisplayLocation());
                return;
            }
        }
    }

    @Override
    public void fixDisplayNeedRegen() {
        respawn();
    }

    @Override
    public boolean isSpawned() {
        if (this.armorStand == null) {
            return false;
        }
        return this.armorStand.isValid();
    }

    private static boolean isTool(Material material) {
        String nlc = material.name().toLowerCase();
        return nlc.contains("sword") || nlc.contains("shovel") || nlc.contains("axe");
    }
}
