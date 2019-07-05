package org.maxgamer.quickshop.Command.SubCommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.*;
import org.maxgamer.quickshop.Command.CommandProcesser;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Shop.ContainerShop;
import org.maxgamer.quickshop.Shop.Shop;
import org.maxgamer.quickshop.Util.MsgUtil;
import org.maxgamer.quickshop.Util.Util;

public class SubCommand_SilentEmpty implements CommandProcesser {
    private QuickShop plugin = QuickShop.instance;

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        if (cmdArg.length < 4) {
            Util.debugLog("Exception on command, cancel.");
            return;
        }
        Shop shop = plugin.getShopManager().getShop(new Location(Bukkit.getWorld(cmdArg[0]), Integer.valueOf(cmdArg[1]),
                Integer.valueOf(cmdArg[2]), Integer.valueOf(cmdArg[3])));
        if (shop != null) {
            if (shop instanceof ContainerShop) {
                ContainerShop cs = (ContainerShop) shop;
                cs.getInventory().clear();
                MsgUtil.sendControlPanelInfo(sender, shop);
                sender.sendMessage(MsgUtil.getMessage("empty-success"));
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        return new ArrayList<>();
    }
}
