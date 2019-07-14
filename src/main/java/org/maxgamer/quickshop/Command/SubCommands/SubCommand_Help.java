package org.maxgamer.quickshop.Command.SubCommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.*;
import org.maxgamer.quickshop.Command.CommandContainer;
import org.maxgamer.quickshop.Command.CommandProcesser;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Util.MsgUtil;
import org.maxgamer.quickshop.Util.Util;

public class SubCommand_Help implements CommandProcesser {
    private QuickShop plugin = QuickShop.instance;

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        return new ArrayList<>();
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        sendHelp(sender, commandLabel);
    }

    private void sendHelp(@NotNull CommandSender s, @NotNull String commandLabel) {
        s.sendMessage(MsgUtil.getMessage("command.description.title"));
        for (CommandContainer container : plugin.getCommandManager().getCmds()) {
            List<String> requirePermissions = container.getPermissions();
            if (container.getPermissions() != null)
                for (String requirePermission : requirePermissions) {
                    if (!s.hasPermission(requirePermission)) {
                        Util.debugLog("Sender " + s
                                .getName() + " trying execute the command: " + commandLabel + ", but no permission " + requirePermission);
                        return;
                    }
                }
            if (!container.isHidden())
                s.sendMessage(ChatColor.GREEN + "/" + commandLabel + " " + container
                        .getPrefix() + ChatColor.YELLOW + " - "
                        + MsgUtil.getMessage("command.description." + container.getPrefix()));
        }
    }
}
