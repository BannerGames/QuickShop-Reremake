package org.maxgamer.quickshop.Command.SubCommands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.maxgamer.quickshop.Command.CommandProcesser;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Shop.Shop;
import org.maxgamer.quickshop.Util.MsgUtil;
import org.maxgamer.quickshop.Util.Util;

import java.util.ArrayList;
import java.util.List;

public class SubCommand_SilentUnlimited implements CommandProcesser {
    private QuickShop plugin = QuickShop.instance;

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        return new ArrayList<>();
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        if (cmdArg.length < 4) {
            Util.debugLog("Exception on command, cancel.");
            return;
        }
        Shop shop = plugin.getShopManager().getShop(new Location(Bukkit.getWorld(cmdArg[0]), Integer.parseInt(cmdArg[1]),
                Integer.parseInt(cmdArg[2]), Integer.parseInt(cmdArg[3])));
        if (shop != null) {
            shop.setUnlimited(!shop.isUnlimited());
            //shop.setSignText();
            shop.update();
            MsgUtil.sendControlPanelInfo(sender, shop);
            sender.sendMessage(MsgUtil.getMessage("command.toggle-unlimited", sender,
                    (shop.isUnlimited() ? plugin.getConfig().getString("shop.toggle-unlimited.unlimited") : plugin.getConfig().getString("shop.toggle-unlimited.limited"))));
            return;
        }
        sender.sendMessage(MsgUtil.getMessage("not-looking-at-shop", sender));
    }
}
