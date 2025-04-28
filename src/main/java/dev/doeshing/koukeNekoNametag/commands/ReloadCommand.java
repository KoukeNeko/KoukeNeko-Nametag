package dev.doeshing.koukeNekoNametag.commands;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    private final KoukeNekoNametag plugin;

    public ReloadCommand(KoukeNekoNametag plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // 檢查權限
        if (!sender.hasPermission("koukeneko.admin")) {
            plugin.getMessageManager().sendConfigMessage(sender, "error.no_permission");
            return true;
        }

        // 確保有參數
        if (args.length < 1) {
            plugin.getMessageManager().sendConfigMessage(sender, "reload.usage", Map.of("label", label));
            return true;
        }

        // 處理指令
        if (args[0].equalsIgnoreCase("reload")) {
            // 重新載入設定文件
            plugin.reloadConfig();

            // 發送重載完成訊息
            plugin.getMessageManager().sendConfigMessage(sender, "reload.success");
        } else {
            // 未知指令
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("command", args[0]);
            plugin.getMessageManager().sendConfigMessage(sender, "error.unknown_subcommand", placeholders);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 只有一個子指令: reload
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
        }

        return completions;
    }
}
