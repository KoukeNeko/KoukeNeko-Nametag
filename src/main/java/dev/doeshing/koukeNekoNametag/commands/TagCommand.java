package dev.doeshing.koukeNekoNametag.commands;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import dev.doeshing.koukeNekoNametag.core.tag.Tag;
import dev.doeshing.koukeNekoNametag.core.tag.TagManager;
import dev.doeshing.koukeNekoNametag.core.tag.TagMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TagCommand implements CommandExecutor, TabCompleter {

    private final KoukeNekoNametag plugin;
    private final TagManager tagManager;
    private final TagMenu tagMenu;

    public TagCommand(KoukeNekoNametag plugin, TagManager tagManager, TagMenu tagMenu) {
        this.plugin = plugin;
        this.tagManager = tagManager;
        this.tagMenu = tagMenu;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // 只有 /tag 命令時，打開選擇菜單（只有玩家可以使用）
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                plugin.getMessageManager().sendMessage(sender, "&c此命令只能由玩家使用！");
                return true;
            }
            
            Player player = (Player) sender;
            tagMenu.openMenu(player);
            return true;
        }
        
        // 所有其他命令需要管理員權限
        if (!sender.hasPermission("koukeneko.admin")) {
            plugin.getMessageManager().sendMessage(sender, "&c你沒有權限使用此命令！");
            return true;
        }
        
        // 處理 /tag create <標籤ID> <顯示格式> 命令
        if (args[0].equalsIgnoreCase("create")) {
            return handleCreateCommand(sender, args);
        }
        
        // 處理 /tag <玩家名稱> add|remove <標籤ID> 格式的命令
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            plugin.getMessageManager().sendMessage(sender, "&c找不到玩家: " + args[0]);
            return true;
        }
        
        if (args.length < 3) {
            plugin.getMessageManager().sendMessage(sender, "&c用法: /tag <玩家名稱> <add|remove> <標籤ID>");
            return true;
        }
        
        String action = args[1].toLowerCase();
        String tagId = args[2];
        Tag tag = tagManager.getTag(tagId);
        
        if (tag == null) {
            plugin.getMessageManager().sendMessage(sender, "&c找不到標籤: " + tagId);
            return true;
        }
        
        switch (action) {
            case "add":
                if (tagManager.giveTagPermission(targetPlayer, tag)) {
                    plugin.getMessageManager().sendMessage(sender, "&a已給予 " + targetPlayer.getName() + " 標籤權限: " + tag.getDisplay());
                    plugin.getMessageManager().sendMessage(targetPlayer, "&a你獲得了標籤權限: " + tag.getDisplay());
                } else {
                    plugin.getMessageManager().sendMessage(sender, "&c無法給予標籤權限！");
                }
                break;
                
            case "remove":
                if (tagManager.removeTagPermission(targetPlayer, tag)) {
                    plugin.getMessageManager().sendMessage(sender, "&a已從 " + targetPlayer.getName() + " 移除標籤權限: " + tag.getDisplay());
                    plugin.getMessageManager().sendMessage(targetPlayer, "&c你的標籤權限已被移除: " + tag.getDisplay());
                } else {
                    plugin.getMessageManager().sendMessage(sender, "&c無法移除標籤權限！");
                }
                break;
                
            default:
                plugin.getMessageManager().sendMessage(sender, "&c未知操作: " + action);
                plugin.getMessageManager().sendMessage(sender, "&c用法: /tag <玩家名稱> <add|remove> <標籤ID>");
                break;
        }
        
        return true;
    }
    
    /**
     * 處理創建標籤的命令
     */
    private boolean handleCreateCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            plugin.getMessageManager().sendMessage(sender, "&c用法: /tag create <標籤ID> <顯示格式>");
            return true;
        }
        
        String tagId = args[1];
        String display = args[2];
        
        if (tagManager.getTag(tagId) != null) {
            plugin.getMessageManager().sendMessage(sender, "&c標籤ID '" + tagId + "' A already exists!");
            return true;
        }
        
        Tag tag = tagManager.createTag(tagId, display);
        plugin.getMessageManager().sendMessage(sender, "&a已創建標籤: " + tag.getDisplay() + " &7(ID: &f" + tag.getId() + "&7)");
        
        return true;
    }
    
    /**
     * 處理重載命令
     */
    private boolean handleReloadCommand(CommandSender sender) {
        tagManager.reload();
        plugin.getMessageManager().sendMessage(sender, "&a標籤系統已重新載入!");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (!sender.hasPermission("koukeneko.admin")) {
            return completions;
        }
        
        if (args.length == 1) {
            // 第一個參數：create 或玩家名稱
            if ("create".startsWith(args[0].toLowerCase())) {
                completions.add("create");
            }
            
            // 加上線上玩家
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
            
            return completions;
        }
        
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                // create 後面的是標籤ID，沒有提示
                return completions;
            }
            
            // 玩家名稱後面是 add/remove
            List<String> actions = List.of("add", "remove");
            for (String action : actions) {
                if (action.startsWith(args[1].toLowerCase())) {
                    completions.add(action);
                }
            }
            
            return completions;
        }
        
        if (args.length == 3) {
            if (!args[0].equalsIgnoreCase("create") && 
                (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                // 如果是 /tag <player> add/remove，第三個參數是標籤ID
                for (Tag tag : tagManager.getAllTags()) {
                    if (tag.getId().toLowerCase().startsWith(args[2].toLowerCase())) {
                        completions.add(tag.getId());
                    }
                }
            }
            
            return completions;
        }
        
        return completions;
    }
}