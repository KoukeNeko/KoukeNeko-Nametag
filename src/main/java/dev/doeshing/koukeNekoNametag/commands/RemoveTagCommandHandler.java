package dev.doeshing.koukeNekoNametag.commands;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import dev.doeshing.koukeNekoNametag.core.tag.Tag;
import dev.doeshing.koukeNekoNametag.core.tag.TagManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 移除標籤指令處理器
 * 
 * 遵循SOLID原則：
 * - SRP (單一責任原則): 專門處理標籤移除邏輯
 * - DIP (依賴反轉原則): 依賴TagManager抽象而非具體實作
 * - OCP (開放封閉原則): 實作CommandHandler介面，可擴展而不修改現有程式碼
 */
public class RemoveTagCommandHandler implements CommandHandler {
    
    private final KoukeNekoNametag plugin;
    private final TagManager tagManager;
    
    public RemoveTagCommandHandler(KoukeNekoNametag plugin, TagManager tagManager) {
        this.plugin = plugin;
        this.tagManager = tagManager;
    }
    
    @Override
    public boolean handle(CommandSender sender, String[] args) {
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
        
        // SRP: 分離處理在線玩家和全域權限移除的邏輯
        int affectedPlayers = handleOnlinePlayersPermissionRemoval(tag);
        handleGlobalPermissionRemoval(tag);
        
        // SRP: 委派給TagManager處理標籤刪除
        if (tagManager.deleteTag(tagId)) {
            sendSuccessMessage(sender, tag, affectedPlayers);
        } else {
            plugin.getMessageManager().sendConfigMessage(sender, "tag.delete_failed");
        }
        
        return true;
    }
    
    /**
     * 處理在線玩家的權限移除
     * SRP: 專門處理在線玩家邏輯
     */
    private int handleOnlinePlayersPermissionRemoval(Tag tag) {
        int affectedPlayers = 0;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(tag.getPermission())) {
                // 移除玩家的標籤權限
                tagManager.removeTagPermission(player, tag);
                
                // 傳送通知給玩家
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("display", tag.getDisplay());
                plugin.getMessageManager().sendConfigMessage(player, "tag.tag_deleted_notice", placeholders);
                affectedPlayers++;
            }
        }
        
        return affectedPlayers;
    }
    
    /**
     * 處理全域權限移除
     * SRP: 專門處理全域權限移除邏輯
     */
    private void handleGlobalPermissionRemoval(Tag tag) {
        List<String> commands = plugin.getConfig().getStringList("command.remove_permission_all");
        for (String cmd : commands) {
            cmd = cmd.replace("{tag}", tag.getPermission())
                   .replace("{display}", tag.getDisplay());
            plugin.getLogger().info("執行移除所有玩家標籤權限指令: " + cmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }
    
    /**
     * 傳送成功訊息
     * SRP: 專門處理成功訊息邏輯
     */
    private void sendSuccessMessage(CommandSender sender, Tag tag, int affectedPlayers) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("display", tag.getDisplay());
        placeholders.put("id", tag.getId());
        plugin.getMessageManager().sendConfigMessage(sender, "tag.deleted", placeholders);
        
        if (affectedPlayers > 0) {
            Map<String, String> countPlaceholders = new HashMap<>();
            countPlaceholders.put("count", String.valueOf(affectedPlayers));
            plugin.getMessageManager().sendConfigMessage(sender, "tag.affected_players", countPlaceholders);
        }
    }
    
    @Override
    public String getCommandName() {
        return "remove";
    }
    
    @Override
    public boolean canHandle(String[] args) {
        return args.length > 0 && "remove".equalsIgnoreCase(args[0]);
    }
}