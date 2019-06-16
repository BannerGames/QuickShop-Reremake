package org.maxgamer.quickshop.Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.*;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Shop.Shop;

@SuppressWarnings("WeakerAccess")
public class MsgUtil {
    private static QuickShop plugin = QuickShop.instance;
    private static YamlConfiguration itemi18n;
    private static YamlConfiguration enchi18n;
    private static YamlConfiguration potioni18n;
    private static HashMap<UUID, LinkedList<String>> player_messages = new HashMap<UUID, LinkedList<String>>();
    private static boolean inited;
    private static YamlConfiguration messagei18n;
    private static File messageFile;

    public static YamlConfiguration getI18nYaml() {
        return messagei18n;
    }

    public static void loadCfgMessages(@NotNull String... reload) {
        /* Check & Load & Create default messages.yml */
        messageFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messageFile.exists()) {
            plugin.getLogger().info("Creating messages.yml");
            plugin.getLanguage().saveFile(plugin.getConfig().getString("language"), "messages", "messages.yml");
        }
        // Store it
        messagei18n = YamlConfiguration.loadConfiguration(messageFile);
        messagei18n.options().copyDefaults(true);

        YamlConfiguration messagei18nYAML = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getLanguage()
                .getFile(plugin.getConfig().getString("language"), "messages")));
        messagei18n.setDefaults(messagei18nYAML);
        /* Set default language vesion and update messages.yml */
        if (messagei18n.getInt("language-version") == 0) {
            messagei18n.set("language-version", 1);
        }
        if (reload.length == 0)
            try {
                updateMessages(messagei18n.getInt("language-version"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        Util.parseColours(messagei18n);

        /* Print to console this language file's author, contributors, and region*/
        if (!inited) {
            plugin.getLogger().info(getMessage("translation-author"));
            plugin.getLogger().info(getMessage("translation-contributors"));
            plugin.getLogger().info(getMessage("translation-country"));
            //plugin.getLogger().info(getMessage("translation-version"));
            inited = true;
        }
        /* Save the upgraded messages.yml */
        try {
            messagei18n.save(messageFile);
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.WARNING, "Could not load/save transaction from messages.yml. Skipping.");
        }

    }

    private static void updateMessages(int selectedVersion) throws IOException {
        if (selectedVersion == 1) {
            messagei18n.set("shop-not-exist", "&cThere had no shop.");
            messagei18n.set("controlpanel.infomation", "&aShop Control Panel:");
            messagei18n.set("controlpanel.setowner", "&aOwner: &b{0} &e[&d&lChange&e]");
            messagei18n.set("controlpanel.setowner-hover", "&eLooking you want changing shop and click to switch owner.");
            messagei18n.set("controlpanel.unlimited", "&aUnlimited: {0} &e[&d&lSwitch&e]");
            messagei18n
                    .set("controlpanel.unlimited-hover", "&eLooking you want changing shop and click to switch enabled or disabled.");
            messagei18n.set("controlpanel.mode-selling", "&aShop mode: &bSelling &e[&d&lSwitch&e]");
            messagei18n
                    .set("controlpanel.mode-selling-hover", "&eLooking you want changing shop and click to switch enabled or disabled.");
            messagei18n.set("controlpanel.mode-buying", "&aShop mode: &bBuying &e[&d&lSwitch&e]");
            messagei18n
                    .set("controlpanel.mode-buying-hover", "&eLooking you want changing shop and click to switch enabled or disabled.");
            messagei18n.set("controlpanel.price", "&aPrice: &b{0} &e[&d&lSet&e]");
            messagei18n.set("controlpanel.price-hover", "&eLooking you want changing shop and click to set new price.");
            messagei18n.set("controlpanel.refill", "&aRefill: Refill the shop items &e[&d&lOK&e]");
            messagei18n.set("controlpanel.refill-hover", "&eLooking you want changing shop and click to refill.");
            messagei18n.set("controlpanel.empty", "&aEmpty: Remove shop all items &e[&d&lOK&e]");
            messagei18n.set("controlpanel.empty-hover", "&eLooking you want changing shop and click to clear.");
            messagei18n.set("controlpanel.remove", "&c&l[Remove Shop]");
            messagei18n.set("controlpanel.remove-hover", "&eClick to remove this shop.");
            messagei18n.set("language-version", 2);
            selectedVersion = 2;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 2) {
            messagei18n.set("command.no-target-given", "&cUsage: /qs export mysql|sqlite");
            messagei18n.set("command.description.debug", "&ePrint debug infomation");
            messagei18n
                    .set("no-permission-remove-shop", "&cYou do not have permission to use that command. Try break the shop instead?");
            messagei18n.set("language-version", 3);
            selectedVersion = 3;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 3) {
            messagei18n.set("signs.unlimited", "Unlimited");
            messagei18n.set("controlpanel.sign.owner.line1", "");
            messagei18n.set("controlpanel.sign.owner.line2", "Enter");
            messagei18n.set("controlpanel.sign.owner.line3", "new owner name");
            messagei18n.set("controlpanel.sign.owner.line4", "at first line");
            messagei18n.set("controlpanel.sign.price.line1", "");
            messagei18n.set("controlpanel.sign.price.line2", "Enter");
            messagei18n.set("controlpanel.sign.price.line3", "new shop price");
            messagei18n.set("controlpanel.sign.price.line4", "at first line");
            messagei18n.set("controlpanel.sign.refill.line1", "");
            messagei18n.set("controlpanel.sign.refill.line2", "Enter amount");
            messagei18n.set("controlpanel.sign.refill.line3", "you want fill");
            messagei18n.set("controlpanel.sign.refill.line4", "at first line");
            messagei18n.set("language-version", 4);
            selectedVersion = 4;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 4) {
            messagei18n.set("signs.unlimited", "Unlimited");
            messagei18n.set("controlpanel.sign", null);
            messagei18n.set("language-version", 5);
            selectedVersion = 5;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 5) {
            messagei18n.set("command.description.fetchmessage", "&eFetch unread shop message");
            messagei18n.set("nothing-to-flush", "&aYou had no new shop message.");
            messagei18n.set("language-version", 6);
            selectedVersion = 6;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 6) {
            messagei18n.set("command.description.info", "&eShow QuickShop Statistics");
            messagei18n.set("command.description.debug", "&eSwitch to developer mode");
            messagei18n.set("break-shop-use-supertool", "&eYou break the shop by use SuperTool.");
            messagei18n
                    .set("no-creative-break", "&cYou cannot break other players shops in creative mode.  Use survival instead or use SuperTool ({0}).");
            messagei18n.set("command.now-debuging", "&aSuccessfully switch to developer mode, Reloading QuickShop...");
            messagei18n.set("command.now-nolonger-debuging", "&aSuccessfully switch to production mode, Reloading QuickShop...");
            messagei18n.set("language-version", 7);
            selectedVersion = 7;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 7) {
            messagei18n.set("failed-to-put-sign", "&cNo enough space around the shop to place infomation sign.");
            messagei18n.set("language-version", 8);
            selectedVersion = 8;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 8) {
            messagei18n
                    .set("failed-to-paste", "&cFailed upload data to Pastebin, Check the internet and try again. (See console for details)");
            messagei18n
                    .set("warn-to-paste", "&eCollecting data and upload to Pastebin, this may need a while. &c&lWarning&c, The data is keep public one week, it may leak your server configuration, make sure you only send it to your &ltrusted staff/developer.");
            messagei18n.set("command.description.paste", "&eAuto upload server data to Pastebin");
            messagei18n.set("language-version", 9);
            selectedVersion = 9;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 9) {
            messagei18n.set("controlpanel.commands.setowner", "/qs owner [Player]");
            messagei18n.set("controlpanel.commands.unlimited", "/qs slientunlimited {0} {1} {2} {3}");
            messagei18n.set("controlpanel.commands.buy", "/qs silentbuy {0} {1} {2} {3}");
            messagei18n.set("controlpanel.commands.sell", "/qs silentsell {0} {1} {2} {3}");
            messagei18n.set("controlpanel.commands.price", "/qs price [New Price]");
            messagei18n.set("controlpanel.commands.refill", "/qs refill [Amount]");
            messagei18n.set("controlpanel.commands.empty", "/qs silentempty {0} {1} {2} {3}");
            messagei18n.set("controlpanel.commands.remove", "/qs silentremove {0} {1} {2} {3}");
            messagei18n.set("tableformat.full_line", "+---------------------------------------------------+");
            messagei18n.set("tableformat.left_half_line", "+--------------------");
            messagei18n.set("tableformat.right_half_line", "--------------------+");
            messagei18n.set("tableformat.left_begin", "| ");
            messagei18n.set("booleanformat.success", "&a✔");
            messagei18n.set("booleanformat.failed", "&c✘");
            messagei18n.set("language-version", 10);
            selectedVersion = 10;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 10) {
            messagei18n.set("price-too-high", "&cShop price too high! You can't create price higher than {0} shop.");
            messagei18n.set("language-version", 11);
            selectedVersion = 11;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 11) {
            messagei18n.set("unknown-player", "&cTarget player not exist, please check username your typed.");
            messagei18n.set("shop-staff-cleared", "&aSuccessfully remove all staff for your shop.");
            messagei18n.set("shop-staff-added", "&aSuccessfully add {0} to your shop staffs.");
            messagei18n.set("shop-staff-deleted", "&aSuccessfully remove {0} to your shop staffs.");
            messagei18n.set("command.wrong-args", "&cParameters not matched, use /qs help to check help");
            messagei18n.set("command.description.staff", "&eManage your shop staffs.");
            messagei18n.set("unknown-player", "&cTarget player not exist, please check username your typed.");
            messagei18n.set("language-version", 12);
            selectedVersion = 12;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 12) {
            messagei18n.set("menu.commands.preview", "/qs silentpreview {0} {1} {2} {3}");
            messagei18n.set("shop-staff-cleared", "&aSuccessfully remove all staff for your shop.");
            messagei18n.set("shop-staff-added", "&aSuccessfully add {0} to your shop staffs.");
            messagei18n.set("shop-staff-deleted", "&aSuccessfully remove {0} to your shop staffs.");
            messagei18n.set("command.wrong-args", "&cParameters not matched, use /qs help to check help");
            messagei18n.set("command.description.staff", "&eManage your shop staffs.");
            messagei18n.set("unknown-player", "&cTarget player not exist, please check username your typed.");
            messagei18n.set("language-version", 13);
            selectedVersion = 13;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 13) {
            messagei18n.set("no-permission-build", "&cYou can't build shop here.");
            messagei18n.set("success-change-owner-to-server", "&aSuccessfully set shop owner to Server.");
            messagei18n.set("updatenotify.buttontitle", "[Update Now]");
            List<String> notifylist = new ArrayList<>();
            notifylist.add("{0} is released, You still using {1}!");
            notifylist.add("Boom! New update {0} incoming, Update!");
            notifylist.add("Surprise! {0} come out, you are on {1}");
            notifylist.add("Looks you need update, {0} is updated!");
            notifylist.add("Ooops! {0} new released, you are {1}!");
            notifylist.add("I promise, QS updated {0}, why not update?");
            notifylist.add("Fixing and re... Sorry {0} is updated!");
            notifylist.add("Err! Nope, not error, just {0} updated!");
            notifylist.add("OMG! {0} come out! Why you still use {1}?");
            notifylist.add("Today News: QuickShop updated {0}!");
            notifylist.add("Plugin K.I.A, You should update {0}!");
            notifylist.add("Fuze is fuzeing update {0}, save update!");
            notifylist.add("You are update commander, told u {0} come out!");
            notifylist.add("Look me style---{0} updated, you still {1}");
            notifylist.add("Ahhhhhhh! New update {0}! Update!");
            notifylist.add("What U thinking? {0} released! Update!");
            messagei18n.set("updatenotify.list", notifylist);
            messagei18n.set("language-version", 14);
            selectedVersion = 14;
            messagei18n.save(messageFile);
        }
        if (selectedVersion == 14) {
            messagei18n.set("flush-finished", "&aSuccessfully flushed the messages.");
            messagei18n.set("language-version", 15);
            selectedVersion = 15;
            messagei18n.save(messageFile);
        }
    }

    /**
     * Send controlPanel infomation to sender
     *
     * @param sender Target sender
     * @param shop   Target shop
     */
    public static void sendControlPanelInfo(@NotNull CommandSender sender, @NotNull Shop shop) {
        if (!sender.hasPermission("quickshop.use")) {
            return;
        }

        if (plugin.getConfig().getBoolean("sneak-to-control"))
            if (sender instanceof Player)
                if (!((Player) sender).isSneaking())
                    return;
        ChatSheetPrinter chatSheetPrinter = new ChatSheetPrinter(sender);
        chatSheetPrinter.printHeader();
        chatSheetPrinter.printLine(MsgUtil.getMessage("controlpanel.infomation"));
        // Owner
        if (!sender.hasPermission("quickshop.setowner")) {
            chatSheetPrinter.printLine(MsgUtil.getMessage("menu.owner", shop.ownerName()));
        } else {
            chatSheetPrinter.printSuggestableCmdLine(MsgUtil.getMessage("controlpanel.setowner", shop.ownerName()), MsgUtil
                    .getMessage("controlpanel.setowner-hover"), MsgUtil.getMessage("controlpanel.commands.setowner"));
        }

        // Unlimited
        if (sender.hasPermission("quickshop.unlimited")) {
            String text = MsgUtil.getMessage("controlpanel.unlimited", bool2String(shop.isUnlimited()));
            String hoverText = MsgUtil.getMessage("controlpanel.unlimited-hover");
            //String clickCommand = "/qs silentunlimited " + shop.getLocation().getWorld().getName() + " "
            //		+ shop.getLocation().getBlockX() + " " + shop.getLocation().getBlockY() + " "
            //		+ shop.getLocation().getBlockZ();
            String clickCommand = MsgUtil.getMessage("controlpanel.commands.unlimited",
                    String.valueOf(shop.getLocation().getWorld().getName()),
                    String.valueOf(shop.getLocation().getBlockX()),
                    String.valueOf(shop.getLocation().getBlockY()),
                    String.valueOf(shop.getLocation().getBlockZ()));
            chatSheetPrinter.printExecuteableCmdLine(text, hoverText, clickCommand);
        }
        // Buying/Selling Mode
        if (sender.hasPermission("quickshop.create.buy") && sender.hasPermission("quickshop.create.sell")) {
            if (shop.isSelling()) {
                String text = MsgUtil.getMessage("controlpanel.mode-selling");
                String hoverText = MsgUtil.getMessage("controlpanel.mode-selling-hover");
                //String clickCommand = "/qs silentbuy " + shop.getLocation().getWorld().getName() + " "
                //		+ shop.getLocation().getBlockX() + " " + shop.getLocation().getBlockY() + " "
                //		+ shop.getLocation().getBlockZ();
                String clickCommand = MsgUtil.getMessage("controlpanel.commands.buy",
                        String.valueOf(shop.getLocation().getWorld().getName()),
                        String.valueOf(shop.getLocation().getBlockX()),
                        String.valueOf(shop.getLocation().getBlockY()),
                        String.valueOf(shop.getLocation().getBlockZ()));
                chatSheetPrinter.printExecuteableCmdLine(text, hoverText, clickCommand);
            } else if (shop.isBuying()) {
                String text = MsgUtil.getMessage("controlpanel.mode-buying");
                String hoverText = MsgUtil.getMessage("controlpanel.mode-buying-hover");
                //String clickCommand = "/qs silentsell " + shop.getLocation().getWorld().getName() + " "
                //		+ shop.getLocation().getBlockX() + " " + shop.getLocation().getBlockY() + " "
                //		+ shop.getLocation().getBlockZ() ;
                String clickCommand = MsgUtil.getMessage("controlpanel.commands.sell",
                        String.valueOf(shop.getLocation().getWorld().getName()),
                        String.valueOf(shop.getLocation().getBlockX()),
                        String.valueOf(shop.getLocation().getBlockY()),
                        String.valueOf(shop.getLocation().getBlockZ()));
                chatSheetPrinter.printExecuteableCmdLine(text, hoverText, clickCommand);
            }
        }
        // Set Price
        if (sender.hasPermission("quickshop.other.price") || shop.getOwner().equals(((Player) sender).getUniqueId())) {
            String text = MsgUtil.getMessage("controlpanel.price", String.valueOf(shop.getPrice()));
            String hoverText = MsgUtil.getMessage("controlpanel.price-hover");
            //String clickCommand = "/qs price [New Price]";
            String clickCommand = MsgUtil.getMessage("controlpanel.commands.price");
            chatSheetPrinter.printSuggestableCmdLine(text, hoverText, clickCommand);
        }
        // Refill
        if (sender.hasPermission("quickshop.refill")) {
            String text = MsgUtil.getMessage("controlpanel.refill", String.valueOf(shop.getPrice()));
            String hoverText = MsgUtil.getMessage("controlpanel.refill-hover");
            //String clickCommand = "/qs refill [Amount]";
            String clickCommand = MsgUtil.getMessage("controlpanel.commands.refill");
            chatSheetPrinter.printSuggestableCmdLine(text, hoverText, clickCommand);
        }
        // Refill
        if (sender.hasPermission("quickshop.empty")) {
            String text = MsgUtil.getMessage("controlpanel.empty", String.valueOf(shop.getPrice()));
            String hoverText = MsgUtil.getMessage("controlpanel.empty-hover");
            //String clickCommand = "/qs silentempty " + shop.getLocation().getWorld().getName() + " "
            //		+ shop.getLocation().getBlockX() + " " + shop.getLocation().getBlockY() + " "
            //		+ shop.getLocation().getBlockZ();
            String clickCommand = MsgUtil.getMessage("controlpanel.commands.empty",
                    String.valueOf(shop.getLocation().getWorld().getName()),
                    String.valueOf(shop.getLocation().getBlockX()),
                    String.valueOf(shop.getLocation().getBlockY()),
                    String.valueOf(shop.getLocation().getBlockZ()));
            chatSheetPrinter.printExecuteableCmdLine(text, hoverText, clickCommand);
        }
        // Remove
        if (sender.hasPermission("quickshop.other.destroy") || shop.getOwner().equals(((Player) sender).getUniqueId())) {
            String text = MsgUtil.getMessage("controlpanel.remove", String.valueOf(shop.getPrice()));
            String hoverText = MsgUtil.getMessage("controlpanel.remove-hover");
            //String clickCommand = "/qs silentremove " + shop.getLocation().getWorld().getName() + " "
            //		+ shop.getLocation().getBlockX() + " " + shop.getLocation().getBlockY() + " "
            //		+ shop.getLocation().getBlockZ();
            String clickCommand = MsgUtil.getMessage("controlpanel.commands.remove",
                    String.valueOf(shop.getLocation().getWorld().getName()),
                    String.valueOf(shop.getLocation().getBlockX()),
                    String.valueOf(shop.getLocation().getBlockY()),
                    String.valueOf(shop.getLocation().getBlockZ()));
            chatSheetPrinter.printExecuteableCmdLine(text, hoverText, clickCommand);
        }

        chatSheetPrinter.printFooter();
    }

    /**
     * Translate boolean value to String, the symbon is changeable by language file.
     *
     * @param bool The boolean value
     * @return The result of translate.
     */
    public static String bool2String(boolean bool) {
        if (bool) {
            return MsgUtil.getMessage("booleanformat.success");
        } else {
            return MsgUtil.getMessage("booleanformat.failed");
        }
    }

    /**
     * loads all player purchase messages from the database.
     */
    public static void loadTransactionMessages() {
        player_messages.clear(); // Delete old messages
        try {
            ResultSet rs = plugin.getDatabaseHelper().selectAllMessages(plugin.getDatabase());
            while (rs.next()) {
                UUID owner = UUID.fromString(rs.getString("owner"));
                String message = rs.getString("message");
                LinkedList<String> msgs = player_messages.get(owner);
                if (msgs == null) {
                    msgs = new LinkedList<String>();
                    player_messages.put(owner, msgs);
                }
                msgs.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.WARNING, "Could not load transaction messages from database. Skipping.");
        }
    }

    /**
     * Load Itemi18n fron file
     */
    public static void loadItemi18n() {
        File itemi18nFile = new File(plugin.getDataFolder(), "itemi18n.yml");
        if (!itemi18nFile.exists()) {
            plugin.getLogger().info("Creating itemi18n.yml");
            plugin.saveResource("itemi18n.yml", true);
        }
        // Store it
        itemi18n = YamlConfiguration.loadConfiguration(itemi18nFile);
        itemi18n.options().copyDefaults(true);
        YamlConfiguration itemi18nYAML = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin
                .getResource("itemi18n.yml")));
        itemi18n.setDefaults(itemi18nYAML);
        Util.parseColours(itemi18n);
        Material[] itemsi18n = Material.values();
        for (Material material : itemsi18n) {
            String itemi18nString = itemi18n.getString("itemi18n." + material.name());
            if (itemi18nString != null && !itemi18nString.isEmpty())
                continue;
            String itemName = material.name();
            String lastItemName = Util.prettifyText(itemName);
            String localizedName = Util.getLocalizedName(new ItemStack(material));
            /* If have localizedName, use localizedName, not our processed name */
            if (localizedName != null)
                lastItemName = localizedName;
            itemi18n.set("itemi18n." + itemName, lastItemName);
            plugin.getLogger().info("Found new items/blocks [" + Util.prettifyText(lastItemName) + "] , adding it to the config...");
        }
        try {
            itemi18n.save(itemi18nFile);
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.WARNING, "Could not load/save transaction itemname from itemi18n.yml. Skipping.");
        }
        plugin.getLogger().info("Complete to load Itemname i18n.");
    }

    /**
     * Get item's i18n name
     *
     * @param itemBukkitName ItemBukkitName(e.g. Material.STONE.name())
     * @return String Item's i18n name.
     */
    public static String getItemi18n(@NotNull String itemBukkitName) {
        Util.debugLog(itemBukkitName);
        if (itemBukkitName.isEmpty())
            return "Item is empty";
        String itemnameI18n = itemi18n.getString("itemi18n." + itemBukkitName);
        if (itemnameI18n != null && !itemnameI18n.isEmpty())
            return itemnameI18n;
        Material material = Material.matchMaterial(itemBukkitName);
        if (material == null)
            return "Material not exist";
        return Util.prettifyText(material.name());
    }

    public static void loadEnchi18n() {
        plugin.getLogger().info("Starting loading Enchantment i18n...");
        File enchi18nFile = new File(plugin.getDataFolder(), "enchi18n.yml");
        if (!enchi18nFile.exists()) {
            plugin.getLogger().info("Creating enchi18n.yml");
            plugin.saveResource("enchi18n.yml", true);
        }
        // Store it
        enchi18n = YamlConfiguration.loadConfiguration(enchi18nFile);
        enchi18n.options().copyDefaults(true);
        YamlConfiguration enchi18nYAML = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.
                getResource("enchi18n.yml")));
        enchi18n.setDefaults(enchi18nYAML);
        Util.parseColours(enchi18n);
        Enchantment[] enchsi18n = Enchantment.values();
        for (Enchantment ench : enchsi18n) {
            String enchi18nString = enchi18n.getString("enchi18n." + ench.getKey().getKey().toString().trim());
            if (enchi18nString != null && !enchi18nString.isEmpty())
                continue;
            String enchName = ench.getKey().getKey();
            enchi18n.set("enchi18n." + enchName, Util.prettifyText(enchName));
            plugin.getLogger().info("Found new ench [" + Util.prettifyText(enchName) + "] , adding it to the config...");
        }
        try {
            enchi18n.save(enchi18nFile);
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.WARNING, "Could not load/save transaction enchname from enchi18n.yml. Skipping.");
        }
        plugin.getLogger().info("Complete to load enchname i18n.");
    }

    /**
     * Get Enchantment's i18n name.
     *
     * @param key The Enchantment.
     * @return Enchantment's i18n name.
     */
    public static String getEnchi18n(@NotNull Enchantment key) {
        String enchString = key.getKey().getKey();
        if (enchString.isEmpty())
            return "Enchantment key is empty";
        String enchI18n = enchi18n.getString("enchi18n." + enchString);
        if (enchI18n != null && !enchI18n.isEmpty())
            return enchI18n;
        return Util.prettifyText(enchString);
    }

    public static void loadPotioni18n() {
        plugin.getLogger().info("Starting loading Potion i18n...");
        File potioni18nFile = new File(plugin.getDataFolder(), "potioni18n.yml");
        if (!potioni18nFile.exists()) {
            plugin.getLogger().info("Creating potioni18n.yml");
            plugin.saveResource("potioni18n.yml", true);
        }
        // Store it
        potioni18n = YamlConfiguration.loadConfiguration(potioni18nFile);
        potioni18n.options().copyDefaults(true);
        YamlConfiguration potioni18nYAML = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin
                .getResource("potioni18n.yml")));
        potioni18n.setDefaults(potioni18nYAML);
        Util.parseColours(potioni18n);
        PotionEffectType[] potionsi18n = PotionEffectType.values();
        for (PotionEffectType potion : potionsi18n) {
            if (potion != null) {
                String potionI18n = potioni18n.getString("potioni18n." + potion.getName().trim());
                if (potionI18n != null && !potionI18n.isEmpty())
                    continue;
                String potionName = potion.getName();
                plugin.getLogger().info("Found new potion [" + Util.prettifyText(potionName) + "] , adding it to the config...");
                potioni18n.set("potioni18n." + potionName, Util.prettifyText(potionName));
            }
        }
        try {
            potioni18n.save(potioni18nFile);
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.WARNING, "Could not load/save transaction potionname from potioni18n.yml. Skipping.");
        }
        plugin.getLogger().info("Complete to load potionname i18n.");
    }

    /**
     * Get potion effect's i18n name.
     *
     * @param potion potionType
     * @return Potion's i18n name.
     */
    public static String getPotioni18n(@NotNull PotionEffectType potion) {
        String potionString = potion.getName().trim();
        if (potionString.isEmpty())
            return "Potion name is empty.";
        String potionI18n = potioni18n.getString("potioni18n." + potionString);
        if (potionI18n != null && !potionI18n.isEmpty())
            return potionI18n;
        return Util.prettifyText(potionString);
    }

    /**
     * @param player  The name of the player to message
     * @param message The message to send them Sends the given player a message if
     *                they're online. Else, if they're not online, queues it for
     *                them in the database.
     * @param isUnlimited  The shop is or unlimited
     */
    public static void send(@NotNull UUID player, @NotNull String message, @NotNull boolean isUnlimited) {
        if (plugin.getConfig().getBoolean("shop.ignore-unlimited-shop-messages") && isUnlimited)
            return; //Ignore unlimited shops messages.
        OfflinePlayer p = Bukkit.getOfflinePlayer(player);
        if (!p.isOnline()) {
            LinkedList<String> msgs = player_messages.get(player);
            // msgs = new LinkedList<String>();
            if (msgs == null)
                msgs = new LinkedList<>();
            player_messages.put(player, msgs);
            msgs.add(message);
            plugin.getDatabaseHelper().sendMessage(plugin.getDatabase(), player, message, System.currentTimeMillis());
        } else {
            if (p.getPlayer() != null)
                p.getPlayer().sendMessage(message);
        }
    }

    /**
     * Deletes any messages that are older than a week in the database, to save
     * on space.
     */
    public static void clean() {
        plugin.getLogger().info("Cleaning purchase messages from the database that are over a week old...");
        // 604800,000 msec = 1 week.
        long weekAgo = System.currentTimeMillis() - 604800000;
        plugin.getDatabaseHelper().cleanMessage(plugin.getDatabase(), weekAgo);
    }

    /**
     * Empties the queue of messages a player has and sends them to the player.
     *
     * @param p The player to message
     * @return True if success, False if the player is offline or null
     */
    public static boolean flush(@NotNull OfflinePlayer p) {    //TODO Changed to UUID
        if (p.isOnline()) {
            UUID pName = p.getUniqueId();
            LinkedList<String> msgs = player_messages.get(pName);
            if (msgs != null) {
                for (String msg : msgs) {
                    if (p.getPlayer() != null)
                        p.getPlayer().sendMessage(msg);
                }
                plugin.getDatabaseHelper().cleanMessageForPlayer(plugin.getDatabase(), pName);
                msgs.clear();
            }
            return true;
        }
        return false;
    }

    /**
     * Send a shop infomation to a player.
     *
     * @param p    Target player
     * @param shop The shop
     */
    public static void sendShopInfo(@NotNull Player p, @NotNull Shop shop) {
        // Potentially faster with an array?
        ItemStack items = shop.getItem();
        ChatSheetPrinter chatSheetPrinter = new ChatSheetPrinter(p);
        chatSheetPrinter.printHeader();
        chatSheetPrinter.printLine(MsgUtil.getMessage("menu.shop-information"));
        chatSheetPrinter.printLine(MsgUtil.getMessage("menu.owner", shop.ownerName()));
        //Enabled
        Util.sendItemholochat(shop, shop.getItem(), p, ChatColor.DARK_PURPLE + MsgUtil
                .getMessage("tableformat.left_begin") + " " + MsgUtil
                .getMessage("menu.item", MsgUtil.getDisplayName(shop.getItem())));
        if (Util.isTool(items.getType())) {
            chatSheetPrinter.printLine(MsgUtil.getMessage("menu.damage-percent-remaining", Util.getToolPercentage(items)));
        }
        if (shop.isSelling()) {
            if (shop.getRemainingStock() == -1) {
                chatSheetPrinter.printLine(MsgUtil.getMessage("menu.stock", "" + MsgUtil.getMessage("signs.unlimited")));
            } else {
                chatSheetPrinter.printLine(MsgUtil.getMessage("menu.stock", "" + shop.getRemainingStock()));
            }
        } else {
            if (shop.getRemainingSpace() == -1) {
                chatSheetPrinter.printLine(MsgUtil.getMessage("menu.space", "" + MsgUtil.getMessage("signs.unlimited")));
            } else {
                chatSheetPrinter.printLine(MsgUtil.getMessage("menu.space", "" + shop.getRemainingSpace()));
            }
        }
        chatSheetPrinter.printLine(MsgUtil
                .getMessage("menu.price-per", MsgUtil.getDisplayName(shop.getItem()), Util.format(shop.getPrice())));
        if (shop.isBuying()) {
            chatSheetPrinter.printLine(MsgUtil.getMessage("menu.this-shop-is-buying"));
        } else {
            chatSheetPrinter.printLine(MsgUtil.getMessage("menu.this-shop-is-selling"));
        }
        Map<Enchantment, Integer> enchs = new HashMap<>();
        if (items.hasItemMeta() && items.getItemMeta().hasEnchants())
            enchs = items.getItemMeta().getEnchants();
        if (!enchs.isEmpty()) {
            chatSheetPrinter.printCenterLine(MsgUtil.getMessage("menu.enchants"));
            for (Entry<Enchantment, Integer> entries : enchs.entrySet()) {
                chatSheetPrinter.printLine(ChatColor.YELLOW + MsgUtil.getEnchi18n(entries.getKey()) + " " + entries.getValue());
            }
        }
        if (items.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta stor = (EnchantmentStorageMeta) items.getItemMeta();
            stor.getStoredEnchants();
            enchs = stor.getStoredEnchants();
            if (!enchs.isEmpty()) {
                chatSheetPrinter.printLine(MsgUtil.getMessage("menu.stored-enchants") + MsgUtil
                        .getMessage("tableformat.right_half_line"));
                for (Entry<Enchantment, Integer> entries : enchs.entrySet()) {
                    chatSheetPrinter.printLine(ChatColor.YELLOW + MsgUtil.getEnchi18n(entries.getKey()) + " " + entries
                            .getValue());
                }
            }
        }
        chatSheetPrinter.printFooter();
    }

    /**
     * Send a purchaseSuccess message for a player.
     *
     * @param p      Target player
     * @param shop   Target shop
     * @param amount Trading item amounts.
     */
    public static void sendPurchaseSuccess(@NotNull Player p, @NotNull Shop shop, @NotNull int amount) {
        ChatSheetPrinter chatSheetPrinter = new ChatSheetPrinter(p);
        chatSheetPrinter.printHeader();
        chatSheetPrinter.printLine(MsgUtil.getMessage("menu.successful-purchase"));
        chatSheetPrinter.printLine(MsgUtil
                .getMessage("menu.item-name-and-price", "" + amount, MsgUtil.getDisplayName(shop.getItem()), Util
                        .format((amount * shop.getPrice()))));
        Map<Enchantment, Integer> enchs = new HashMap<>();
        if (shop.getItem().hasItemMeta() && shop.getItem().getItemMeta().hasEnchants())
            enchs = shop.getItem().getItemMeta().getEnchants();
        if (!enchs.isEmpty()) {
            chatSheetPrinter.printCenterLine(MsgUtil.getMessage("menu.enchants"));
            for (Entry<Enchantment, Integer> entries : enchs.entrySet()) {
                chatSheetPrinter.printLine(ChatColor.YELLOW + MsgUtil.getEnchi18n(entries.getKey()));
            }
        }
        if (shop.getItem().getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta stor = (EnchantmentStorageMeta) shop.getItem().getItemMeta();
            stor.getStoredEnchants();
            enchs = stor.getStoredEnchants();
            if (!enchs.isEmpty()) {
                chatSheetPrinter.printCenterLine(MsgUtil.getMessage("menu.stored-enchants"));
                for (Entry<Enchantment, Integer> entries : enchs.entrySet()) {
                    chatSheetPrinter.printLine(ChatColor.YELLOW + MsgUtil.getEnchi18n(entries.getKey()));
                }
            }
        }
        chatSheetPrinter.printFooter();
    }

    /**
     * Send a sellSuccess message for a player.
     *
     * @param p      Target player
     * @param shop   Target shop
     * @param amount Trading item amounts.
     */
    public static void sendSellSuccess(@NotNull Player p, @NotNull Shop shop, int amount) {
        ChatSheetPrinter chatSheetPrinter = new ChatSheetPrinter(p);
        chatSheetPrinter.printHeader();
        chatSheetPrinter.printLine(MsgUtil.getMessage("menu.successfully-sold"));
        chatSheetPrinter.printLine(MsgUtil
                .getMessage("menu.item-name-and-price", "" + amount, MsgUtil.getDisplayName(shop.getItem()), Util
                        .format((amount * shop.getPrice()))));
        if (plugin.getConfig().getBoolean("show-tax")) {
            double tax = plugin.getConfig().getDouble("tax");
            double total = amount * shop.getPrice();
            if (tax != 0) {
                if (!p.getUniqueId().equals(shop.getOwner())) {
                    chatSheetPrinter.printLine(MsgUtil.getMessage("menu.sell-tax", Util.format((tax * total))));
                } else {
                    chatSheetPrinter.printLine(MsgUtil.getMessage("menu.sell-tax-self"));
                }
            }
        }
        Map<Enchantment, Integer> enchs = new HashMap<>();
        if (shop.getItem().hasItemMeta() && shop.getItem().getItemMeta().hasEnchants())
            enchs = shop.getItem().getItemMeta().getEnchants();
        if (!enchs.isEmpty()) {
            chatSheetPrinter.printCenterLine(MsgUtil.getMessage("menu.enchants"));
            for (Entry<Enchantment, Integer> entries : enchs.entrySet()) {
                chatSheetPrinter.printLine(ChatColor.YELLOW + MsgUtil.getEnchi18n(entries.getKey()));
            }
        }
        if (shop.getItem().getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta stor = (EnchantmentStorageMeta) shop.getItem().getItemMeta();
            stor.getStoredEnchants();
            enchs = stor.getStoredEnchants();
            if (!enchs.isEmpty()) {
                chatSheetPrinter.printCenterLine(MsgUtil.getMessage("menu.stored-enchants"));
                for (Entry<Enchantment, Integer> entries : enchs.entrySet()) {
                    chatSheetPrinter.printLine(ChatColor.YELLOW + MsgUtil.getEnchi18n(entries.getKey()));
                }
            }
        }
        chatSheetPrinter.printFooter();
    }

    /**
     * Get item's displayname.
     * @param iStack stack
     * @return itemDisplayName
     */
    public static String getDisplayName(@NotNull ItemStack iStack) {
        ItemStack is = iStack.clone();
        if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            return is.getItemMeta().getDisplayName();
        } else {
            return MsgUtil.getItemi18n(is.getType().name());
        }

    }

    /**
     * getMessage in messages.yml
     *
     * @param loc location
     * @param args args
     * @return message
     */
    public static String getMessage(@NotNull String loc, @NotNull String... args) {
        String raw = messagei18n.getString(loc);
        if (raw == null) {
            return "Invalid message: " + loc + " Did you modify the i18n file?";
        }
        return fillArgs(raw, args);
    }

    /**
     * Replace args in raw to args
     *
     * @param raw  text
     * @param args args
     * @return filled text
     */
    public static String fillArgs(@Nullable String raw, @Nullable String... args) {
        if (raw == null) {
            return "Invalid message: " + "raw";
        }
        if (raw.isEmpty()) {
            return "";
        }
        if (args == null) {
            return raw;
        }
        for (int i = 0; i < args.length; i++) {
            raw = StringUtils.replace(raw, "{" + i + "}", args[i] == null ? "" : args[i]);
        }
        return raw;
    }

    /**
     * Send the display-item exploit alert for a location.
     *
     * @param objectDo It is possible be Player, Inventory.
     * @param action   What action trigger the exploit alert.
     * @param location Event/Shop location.
     */
    public static void sendExploitAlert(@NotNull Object objectDo, @NotNull String action, @NotNull Location location) {
        Util.sendMessageToOps(ChatColor.RED + "[QuickShop][ExploitAlert] A displayItem exploit was found!");
        if (objectDo instanceof Player) {
            Player player = (Player) objectDo;
            Util.sendMessageToOps(ChatColor.RED + "Exploiter: " + "Player=" + player.getName());
        }
        if (objectDo instanceof Inventory) {
            Inventory inventory = (Inventory) objectDo;
            if (inventory.getHolder() instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) inventory;
                if (livingEntity instanceof Tameable) {
                    Tameable tamedEntity = (Tameable) livingEntity;
                    AnimalTamer tamer = tamedEntity.getOwner();
                    if (tamer != null) {
                        Util.sendMessageToOps(ChatColor.RED + "Exploiter: " + "LivingEntity=" + livingEntity.getType()
                                .name() + "; Tamer=" + Bukkit.getOfflinePlayer(tamer.getUniqueId()));
                    } else {
                        Util.sendMessageToOps(ChatColor.RED + "Exploiter: " + "LivingEntity=" + livingEntity.getType()
                                .name());
                    }
                }
                Util.sendMessageToOps(ChatColor.RED + "Exploiter: " + "LivingEntity=" + livingEntity.getType().name());
            }
            if (inventory.getHolder() instanceof Block) {
                Block block = (Block) inventory;
                Util.sendMessageToOps(ChatColor.RED + "Exploiter: " + "Block=" + block.getType().name());
            }
            Util.sendMessageToOps(ChatColor.RED + "Exploiter: Unknown Inventory");
        }
        Util.sendMessageToOps(ChatColor.RED + "Action: " + action);
        Util.sendMessageToOps(ChatColor.RED + "Location: " + "World=" + location.getWorld().getName() + " X=" + location
                .getBlockX() + " Y=" + location.getBlockY() + " Z=" + location.getBlockZ());
    }
}
