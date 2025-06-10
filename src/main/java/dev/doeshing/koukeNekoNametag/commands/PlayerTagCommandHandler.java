package dev.doeshing.koukeNekoNametag.commands;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import dev.doeshing.koukeNekoNametag.core.tag.Tag;
import dev.doeshing.koukeNekoNametag.core.tag.TagManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家標籤操作指令處理器
 * 
 * 遵循SOLID原則：
 * - SRP (單一責任原則): 專門處理玩家標籤權限操作
 * - DIP (依賴反轉原則): 依賴TagManager抽象而非具體實作
 * - OCP (開放封閉原則): 實作CommandHandler介面，可擴展而不修改現有程式碼
 */
public class PlayerTagCommandHandler implements CommandHandler {
    
    private final KoukeNekoNametag plugin;
    private final TagManager tagManager;
    
    public PlayerTagCommandHandler(KoukeNekoNametag plugin, TagManager tagManager) {
        this.plugin = plugin;
        this.tagManager = tagManager;
    }
    
    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length < 3) {
            plugin.getMessageManager().sendConfigMessage(sender, "tag.help");
            return true;
        }
        
        String playerName = args[0];
        String action = args[1].toLowerCase();
        String tagId = args[2];
        
        // SRP: 委派給專門的方法處理玩家查找
        Player targetPlayer = findTargetPlayer(sender, playerName);
        if (targetPlayer == null) {
            return true;
        }
        
        // SRP: 委派給專門的方法處理標籤查找
        Tag tag = findTag(sender, tagId);
        if (tag == null) {
            return true;
        }
        
        // SRP: 根據操作類型委派給對應的處理方法
        switch (action) {
            case "add":
                return handleAddPermission(sender, targetPlayer, tag);
            case "remove":
                return handleRemovePermission(sender, targetPlayer, tag);
            default:
                return handleUnknownAction(sender, action);
        }
    }
    
    /**
     * 查找目標玩家
     * SRP: 專門處理玩家查找邏輯
     */
    private Player findTargetPlayer(CommandSender sender, String playerName) {
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", playerName);
            plugin.getMessageManager().sendConfigMessage(sender, "tag.player_not_found", placeholders);
        }
        return targetPlayer;
    }
    
    /**
     * 查找標籤
     * SRP: 專門處理標籤查找邏輯
     */
    private Tag findTag(CommandSender sender, String tagId) {
        Tag tag = tagManager.getTag(tagId);
        if (tag == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("tag", tagId);
            plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_not_found", placeholders);
        }
        return tag;
    }
    
    /**
     * 處理新增權限操作
     * SRP: 專門處理新增權限邏輯
     */
    private boolean handleAddPermission(CommandSender sender, Player targetPlayer, Tag tag) {
        if (tagManager.giveTagPermission(targetPlayer, tag)) {
            sendPermissionAddedMessage(sender, targetPlayer, tag);
        } else {
            plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_add_failed");
        }
        return true;
    }
    
    /**
     * 處理移除權限操作
     * SRP: 專門處理移除權限邏輯
     */
    private boolean handleRemovePermission(CommandSender sender, Player targetPlayer, Tag tag) {
        if (tagManager.removeTagPermission(targetPlayer, tag)) {
            sendPermissionRemovedMessage(sender, targetPlayer, tag);
        } else {
            plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_remove_failed");
        }
        return true;
    }
    
    /**
     * 處理未知操作
     * SRP: 專門處理錯誤情況
     */
    private boolean handleUnknownAction(CommandSender sender, String action) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("action", action);
        plugin.getMessageManager().sendConfigMessage(sender, "tag.unknown_action", placeholders);
        plugin.getMessageManager().sendConfigMessage(sender, "tag.help");
        return true;
    }
    
    /**
     * 傳送權限新增成功訊息
     * SRP: 專門處理訊息傳送邏輯
     */
    private void sendPermissionAddedMessage(CommandSender sender, Player targetPlayer, Tag tag) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", targetPlayer.getName());
        placeholders.put("display", tag.getDisplay());
        plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_added", placeholders);
        
        Map<String, String> playerPlaceholders = new HashMap<>();
        playerPlaceholders.put("display", tag.getDisplay());
        plugin.getMessageManager().sendConfigMessage(targetPlayer, "tag.you_got_tag", playerPlaceholders);
    }
    
    /**
     * 傳送權限移除成功訊息
     * SRP: 專門處理訊息傳送邏輯
     */
    private void sendPermissionRemovedMessage(CommandSender sender, Player targetPlayer, Tag tag) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", targetPlayer.getName());
        placeholders.put("display", tag.getDisplay());
        plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_removed", placeholders);
        
        Map<String, String> playerPlaceholders = new HashMap<>();
        playerPlaceholders.put("display", tag.getDisplay());
        plugin.getMessageManager().sendConfigMessage(targetPlayer, "tag.your_tag_removed", playerPlaceholders);
    }
    
    @Override
    public String getCommandName() {
        return "player-operation";
    }
    
    @Override
    public boolean canHandle(String[] args) {
        // 檢查是否為玩家操作格式: <玩家名稱> <add|remove> <標籤ID>
        if (args.length >= 3) {
            String action = args[1].toLowerCase();
            return "add".equals(action) || "remove".equals(action);
        }
        return false;
    }
}