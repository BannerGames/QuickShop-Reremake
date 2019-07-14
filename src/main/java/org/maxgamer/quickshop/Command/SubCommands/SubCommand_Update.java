package org.maxgamer.quickshop.Command.SubCommands;

import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.*;
import org.maxgamer.quickshop.Command.CommandProcesser;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Util.UpdateInfomation;
import org.maxgamer.quickshop.Util.Updater;

public class SubCommand_Update implements CommandProcesser {
    QuickShop plugin = QuickShop.instance;

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sender.sendMessage(ChatColor.YELLOW + "Checking for updates...");
                UpdateInfomation updateInfomation = Updater.checkUpdate();
                String updateVersion = updateInfomation.getVersion();
                if (updateVersion == null) {
                    sender.sendMessage(ChatColor.RED + "Failed check the update, connection issue?");
                    return;
                }
                if (updateVersion.equals(plugin.getDescription().getVersion())) {
                    sender.sendMessage(ChatColor.GREEN + "No updates can update now.");
                    return;
                }
                sender.sendMessage(ChatColor.YELLOW + "Downloading update, this may need a while...");
                byte[] pluginBin;
                try {
                    pluginBin = Updater.downloadUpdatedJar();
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + "Update failed, get details to look the console.");
                    plugin.getSentryErrorReporter().ignoreThrow();
                    e.printStackTrace();
                    return;
                }
                if (pluginBin == null || pluginBin.length < 1) {
                    sender.sendMessage(ChatColor.RED + "Download failed, check your connection before contact the author.");
                    return;
                }
                sender.sendMessage(ChatColor.YELLOW + "Installing update...");
                try {
                    Updater.replaceTheJar(pluginBin);
                } catch (IOException ioe) {
                    sender.sendMessage(ChatColor.RED + "Update failed, get details to look the console.");
                    plugin.getSentryErrorReporter().ignoreThrow();
                    ioe.printStackTrace();
                    return;
                } catch (RuntimeException re) {
                    sender.sendMessage(ChatColor.RED + "Update failed, " + re.getMessage());
                    return;
                }
                sender.sendMessage(ChatColor.GREEN + "Successfully, restart your server to apply the changes!");
            }
        }.runTaskAsynchronously(plugin);

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        return null;
    }
}
