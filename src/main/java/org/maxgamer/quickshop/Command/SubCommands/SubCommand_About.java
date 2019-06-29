package org.maxgamer.quickshop.Command.SubCommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.*;
import org.maxgamer.quickshop.Command.CommandProcesser;
import org.maxgamer.quickshop.QuickShop;

public class SubCommand_About implements CommandProcesser {
    private QuickShop plugin = QuickShop.instance;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        sender.sendMessage("[QuickShop] About QuickShop");
        sender.sendMessage("[QuickShop] Hello, I'm Ghost_chu Author of QS reremake.");
        sender.sendMessage("[QuickShop] This plugin is a remake by the SunnySide Community.");
        sender.sendMessage("[QuickShop] Original author is KaiNoMood. This is an unofficial QS version.");
        sender.sendMessage("[QuickShop] It has more feature, and has been designed for 1.13 and newer versions.");
        sender.sendMessage("[QuickShop] You can look at our SpigotMC page to learn more:");
        sender.sendMessage("[QuickShop] https://www.spigotmc.org/resources/62575/");
        sender.sendMessage("[QuickShop] Thanks for using QuickShop-Reremake.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        return new ArrayList<>();
    }
}
