package org.maxgamer.quickshop.Shop;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.*;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Util.Timer;
import org.maxgamer.quickshop.Util.Util;

/**
 * A class allow plugin load shops fast and simply.
 */
public class ShopLoader {
    private QuickShop plugin;
    private int errors;
    final private ArrayList<Long> loadTimes = new ArrayList<>();
    private int totalLoaded = 0;
    private int loadAfterChunkLoaded = 0;
    private int loadAfterWorldLoaded = 0;

    /**
     * The shop load allow plugin load shops fast and simply.
     *
     * @param plugin Plugin main class
     */
    public ShopLoader(@NotNull QuickShop plugin) {
        this.plugin = plugin;
    }

    public void loadShops() {
        loadShops(null);
    }
    /**
     * Load all shops
     */
    public void loadShops(String worldName) {

        Timer totalLoadTimer = new Timer(true);
        try {
            this.plugin.getLogger().info("Loading shops from the database...");
            Timer fetchTimer = new Timer(true);
            ResultSet rs = plugin.getDatabaseHelper().selectAllShops(this.plugin.getDatabase());
            this.plugin.getLogger().info("Used " + fetchTimer.endTimer() + "ms to fetch all shops from the database.");
            while (rs.next()) {
                Timer singleShopLoadTimer = new Timer(true);

                ShopDatabaseInfoOrigin origin = new ShopDatabaseInfoOrigin(rs);
                if (worldName != null && !origin.getWorld().equals(worldName))
                    continue;
                ShopDatabaseInfo data = new ShopDatabaseInfo(origin);

                Shop shop = new ContainerShop(data.getLocation(), data.getPrice(), data.getItem(), data.getModerators(), data
                        .isUnlimited(), data
                        .getType());

                if (shopNullCheck(shop)) {
                    Util.debugLog("Somethings gone wrong, skipping the loading...");
                    loadAfterWorldLoaded++;
                    singleShopLoaded(singleShopLoadTimer);
                    continue;
                }
                //Load to RAM
                plugin.getShopManager().loadShop(data.getWorld().getName(), shop);

                boolean chunkLoaded = Util.isLoaded(shop.getLocation());

                if (chunkLoaded) {
                    //Load to World
                    if (!Util.canBeShop(shop.getLocation().getBlock())) {
                        Util.debugLog("Target block can't be a shop, removing it from the database...");
                        //shop.delete();
                        plugin.getDatabaseHelper().removeShop(plugin.getDatabase(), shop.getLocation().getBlockX(), shop
                                .getLocation().getBlockY(), shop.getLocation().getBlockZ(), shop.getLocation().getWorld()
                                .getName());
                        singleShopLoaded(singleShopLoadTimer);
                        continue;
                    }

                    plugin.getQueuedShopManager()
                            .add(new QueueShopObject(shop, QueueAction.LOAD, QueueAction.SETSIGNTEXT));
                } else {
                    loadAfterChunkLoaded++;
                }
                singleShopLoaded(singleShopLoadTimer);
            }
            long totalUsedTime = totalLoadTimer.endTimer();
            long avgPerShop = mean(loadTimes.toArray(new Long[0]));
            this.plugin.getLogger()
                    .info("Successfully loaded " + totalLoaded + " shops! (Used " + totalUsedTime + "ms, Avg " + avgPerShop + "ms per shop)");
            this.plugin.getLogger()
                    .info(this.loadAfterChunkLoaded + " shops will load after chunk have loaded, " + this.loadAfterWorldLoaded + " shops will load after the world has loaded.");
        } catch (Exception e) {
            exceptionHandler(e, null);
        }
    }

    private Long mean(Long[] m) {
        long sum = 0;
        for (Long aM : m) {
            sum += aM;
        }
        if (m.length == 0)
            return sum;
        return sum / m.length;
    }

    private void singleShopLoaded(@NotNull Timer singleShopLoadTimer) {
        totalLoaded++;
        long singleShopLoadTime = singleShopLoadTimer.endTimer();
        loadTimes.add(singleShopLoadTime);
        Util.debugLog("Loaded shop used time " + singleShopLoadTime + "ms");
    }

    private boolean shopNullCheck(@Nullable Shop shop) {
        if (shop == null) {
            Util.debugLog("Shop Object is null");
            return true;
        }
        if (shop.getItem() == null) {
            Util.debugLog("Shop ItemStack is null");
            return true;
        }
        if (shop.getLocation() == null) {
            Util.debugLog("Shop Location is null");
            return true;
        }
        if (shop.getLocation().getWorld() == null) {
            Util.debugLog("Shop World is null");
            return true;
        }
        // if (shop.getLocation().getChunk() == null) {
        //     Util.debugLog("Shop Chunk is null");
        //     return true;
        // }
        // if (shop.getLocation().getBlock() == null) {
        //     Util.debugLog("Shop Block is null");
        //     return true;
        // }
        return false;
    }

    private void exceptionHandler(@NotNull Exception ex, @Nullable Location shopLocation) {
        errors++;
        Logger logger = plugin.getLogger();
        logger.warning("##########FAILED TO LOAD SHOP##########");
        logger.warning("  >> Error Info:");
        logger.warning(ex.getMessage());
        logger.warning("  >> Error Trace");
        ex.printStackTrace();
        logger.warning("  >> Target Location Info");
        logger.warning("Location: " + ((shopLocation == null) ? "NULL" : shopLocation.toString()));
        logger.warning("Block: " + ((shopLocation == null) ? "NULL" : shopLocation.getBlock().getType().name()));
        logger.warning("  >> Database Info");
        try {
            logger.warning("Connected: " + String.valueOf(plugin.getDatabase().getConnection().isClosed()));
        } catch (SQLException | NullPointerException e) {
            logger.warning("Connected: " + "FALSE - Failed to load status.");
        }

        try {
            logger.warning("Readonly: " + String.valueOf(plugin.getDatabase().getConnection().isReadOnly()));
        } catch (SQLException | NullPointerException e) {
            logger.warning("Readonly: " + "FALSE - Failed to load status.");
        }

        try {
            logger.warning("ClientInfo: " + String.valueOf(plugin.getDatabase().getConnection().getClientInfo().toString()));
        } catch (SQLException | NullPointerException e) {
            logger.warning("ClientInfo: " + "FALSE - Failed to load status.");
        }

        logger.warning("#######################################");
        if (errors > 10)
            logger.severe("QuickShop detected too many errors when loading shops, you should backup your shop database and ask the developer for help");
    }

    @Getter
    @Setter
    class ShopDatabaseInfoOrigin {
        private int x;
        private int y;
        private int z;
        private String world;
        private String item;
        private String moderators;
        private double price;
        private int type;
        private boolean unlimited;

        public ShopDatabaseInfoOrigin(ResultSet rs) {
            try {
                this.x = rs.getInt("x");
                this.y = rs.getInt("y");
                this.z = rs.getInt("z");
                this.world = rs.getString("world");
                this.item = rs.getString("itemConfig");
                this.moderators = rs.getString("owner");
                this.price = rs.getDouble("price");
                this.type = rs.getInt("type");
                this.unlimited = rs.getBoolean("unlimited");
            } catch (SQLException sqlex) {
                exceptionHandler(sqlex, null);
            }

        }
    }

    @Getter
    @Setter
    class ShopDatabaseInfo {
        private int x;
        private int y;
        private int z;
        private double price;
        private boolean unlimited;
        private ShopType type;
        private World world;
        private ItemStack item;
        private ShopModerator moderators;
        private Location location;

        public ShopDatabaseInfo(ShopDatabaseInfoOrigin origin) {
            try {
                this.x = origin.getX();
                this.y = origin.getY();
                this.z = origin.getZ();
                this.price = origin.getPrice();
                this.unlimited = origin.isUnlimited();
                this.type = ShopType.fromID(origin.getType());
                this.world = Bukkit.getWorld(origin.getWorld());
                this.item = deserializeItem(origin.getItem());
                this.moderators = deserializeModerator(origin.getModerators());
                this.location = new Location(world, x, y, z);
            } catch (Exception ex) {
                exceptionHandler(ex, this.location);
            }
        }

        private ItemStack deserializeItem(@NotNull String itemConfig) {
            try {
                return Util.deserialize(itemConfig);
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
                plugin.getLogger().warning("Failed load shop data, because target config can't deserialize the ItemStack.");
                Util.debugLog("Failed to load data to the ItemStack: " + itemConfig);
                return null;
            }
        }

        private ShopModerator deserializeModerator(@NotNull String moderatorJson) {
            // try {
            //     UUID.fromString(moderators);
            //     if (!isBackuped) {
            //         isBackuped = Util.backupDatabase();
            //     }
            //
            //
            //     moderators = ShopModerator.serialize(shopModerator); //Serialize
            // } catch (IllegalArgumentException ex) {
            //     //This expcetion is normal, cause i need check that is or not a UUID.
            //     shopModerator = ShopModerator.deserialize(moderators);
            // }
            //
            ShopModerator shopModerator;
            if (Util.isUUID(moderatorJson)) {
                Util.debugLog("Updating old shop data...");
                shopModerator = new ShopModerator(UUID.fromString(moderatorJson)); //New one
            } else {
                shopModerator = ShopModerator.deserialize(moderatorJson);
            }
            return shopModerator;
        }
    }
}
