package dev.doeshing.koukeNekoNametag.commands;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import dev.doeshing.koukeNekoNametag.core.lang.LanguageManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // 只有 /tag 指令時，打開選擇選單（只有玩家可以使用）
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                plugin.getMessageManager().sendConfigMessage(sender, "error.player_only");
                return true;
            }
            
            Player player = (Player) sender;
            tagMenu.openMenu(player);
            return true;
        }
        
        // 所有其他指令需要管理員權限
        if (!sender.hasPermission("koukeneko.admin")) {
            plugin.getMessageManager().sendConfigMessage(sender, "error.no_permission");
            return true;
        }
        
        // 處理 /tag create <標籤ID> <顯示格式> 指令
        if (args[0].equalsIgnoreCase("create")) {
            return handleCreateCommand(sender, args);
        }
        
        // 處理 /tag remove <標籤ID> 指令
        if (args[0].equalsIgnoreCase("remove")) {
            return handleRemoveTagCommand(sender, args);
        }
        
        // 處理 /tag <玩家名稱> add|remove <標籤ID> 格式的指令
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", args[0]);
            plugin.getMessageManager().sendConfigMessage(sender, "tag.player_not_found", placeholders);
            return true;
        }
        
        if (args.length < 3) {
            plugin.getMessageManager().sendConfigMessage(sender, "tag.help");
            return true;
        }
        
        String action = args[1].toLowerCase();
        String tagId = args[2];
        Tag tag = tagManager.getTag(tagId);
        
        if (tag == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("tag", tagId);
            plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_not_found", placeholders);
            return true;
        }
        
        switch (action) {
            case "add":
                if (tagManager.giveTagPermission(targetPlayer, tag)) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("player", targetPlayer.getName());
                    placeholders.put("display", tag.getDisplay());
                    plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_added", placeholders);
                    
                    Map<String, String> playerPlaceholders = new HashMap<>();
                    playerPlaceholders.put("display", tag.getDisplay());
                    plugin.getMessageManager().sendConfigMessage(targetPlayer, "tag.you_got_tag", playerPlaceholders);
                } else {
                    plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_add_failed");
                }
                break;
                
            case "remove":
                if (tagManager.removeTagPermission(targetPlayer, tag)) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("player", targetPlayer.getName());
                    placeholders.put("display", tag.getDisplay());
                    plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_removed", placeholders);
                    
                    Map<String, String> playerPlaceholders = new HashMap<>();
                    playerPlaceholders.put("display", tag.getDisplay());
                    plugin.getMessageManager().sendConfigMessage(targetPlayer, "tag.your_tag_removed", playerPlaceholders);
                } else {
                    plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_remove_failed");
                }
                break;
                
            default:
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("action", action);
                plugin.getMessageManager().sendConfigMessage(sender, "tag.unknown_action", placeholders);
                plugin.getMessageManager().sendConfigMessage(sender, "tag.help");
                break;
        }
        
        return true;
    }
    
    /**
     * 處理建立標籤的指令
     */
    private boolean handleCreateCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            plugin.getMessageManager().sendConfigMessage(sender, "tag.create_usage");
            return true;
        }
        
        String tagId = args[1];
        String display = args[2];
        
        if (tagManager.getTag(tagId) != null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("tag", tagId);
            plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_exists", placeholders);
            return true;
        }
        
        Tag tag = tagManager.createTag(tagId, display);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("display", tag.getDisplay());
        placeholders.put("id", tag.getId());
        plugin.getMessageManager().sendConfigMessage(sender, "tag.created", placeholders);
        
        return true;
    }
    
    /**
     * 處理刪除標籤的指令
     */
    private boolean handleRemoveTagCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().sendConfigMessage(sender, "tag.remove_usage");
            return true;
        }
        
        String tagId = args[1];
        Tag tag = tagManager.getTag(tagId);
        
        if (tag == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("tag", tagId);
            plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_not_exists", placeholders);
            return true;
        }
        
        // 找出所有在線且擁有此標籤權限的玩家
        int affectedPlayers = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(tag.getPermission())) {
                // 移除玩家的標籤權限
                tagManager.removeTagPermission(player, tag);
                
                // 如果玩家正在使用此標籤，也移除前綴
//                tagManager.removeActiveTag(player);
                // 傳送通知給玩家哪個標籤已在系統中被移除，但仍然可以使用到想變更前
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("display", tag.getDisplay());
                plugin.getMessageManager().sendConfigMessage(player, "tag.tag_deleted_notice", placeholders);
                affectedPlayers++;
            }
        }
        
        // 對離線玩家，利用LuckPerms API移除權限
        List<String> commands = plugin.getConfig().getStringList("command.remove_permission_all");
        for (String cmd : commands) {
            cmd = cmd.replace("{tag}", tag.getPermission())
                   .replace("{display}", tag.getDisplay());
            plugin.getLogger().info("執行移除所有玩家標籤權限指令: " + cmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        
        // 刪除標籤
        if (tagManager.deleteTag(tagId)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("display", tag.getDisplay());
            placeholders.put("id", tagId);
            plugin.getMessageManager().sendConfigMessage(sender, "tag.deleted", placeholders);
            
            if (affectedPlayers > 0) {
                Map<String, String> countPlaceholders = new HashMap<>();
                countPlaceholders.put("count", String.valueOf(affectedPlayers));
                plugin.getMessageManager().sendConfigMessage(sender, "tag.affected_players", countPlaceholders);
            }
        } else {
            plugin.getMessageManager().sendConfigMessage(sender, "tag.delete_failed");
        }
        
        return true;
    }
    
    /**
     * 處理重載指令
     */
    private boolean handleReloadCommand(CommandSender sender) {
        tagManager.reload();
        plugin.getMessageManager().sendConfigMessage(sender, "reload.tag_system_reloaded");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (!sender.hasPermission("koukeneko.admin")) {
            return completions;
        }
        
        if (args.length == 1) {
            // 第一個參數：create, remove 或玩家名稱
            if ("create".startsWith(args[0].toLowerCase())) {
                completions.add("create");
            }
            if ("remove".startsWith(args[0].toLowerCase())) {
                completions.add("remove");
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
            
            if (args[0].equalsIgnoreCase("remove")) {
                // remove 後面的是標籤ID
                for (Tag tag : tagManager.getAllTags()) {
                    if (tag.getId().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(tag.getId());
                    }
                }
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