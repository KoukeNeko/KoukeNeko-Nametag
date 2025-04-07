package dev.doeshing.koukeNekoNametag.commands;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    private final KoukeNekoNametag plugin;

    public ReloadCommand(KoukeNekoNametag plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // 檢查權限
        if (!sender.hasPermission("koukeneko.admin")) {
            plugin.getMessageManager().sendMessage(sender, "&c你沒有權限使用此命令!");
            return true;
        }

        // 確保有參數
        if (args.length < 1) {
            plugin.getMessageManager().sendMessage(sender, "&c用法: /" + label + " reload");
            return true;
        }

        // 處理命令
        if (args[0].equalsIgnoreCase("reload")) {
            // 重新載入配置文件
            plugin.reloadConfig();

            // 發送重載完成訊息
            plugin.getMessageManager().sendMessage(sender, "&a插件設定成功重新載入!");
        } else {
            // 未知指令
            plugin.getMessageManager().sendMessage(sender, "&c未知的子命令: " + args[0]);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 只有一個子命令: reload
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
        }

        return completions;
    }
}
