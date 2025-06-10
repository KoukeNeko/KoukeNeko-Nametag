package dev.doeshing.koukeNekoNametag.commands;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import dev.doeshing.koukeNekoNametag.core.tag.Tag;
import dev.doeshing.koukeNekoNametag.core.tag.TagManager;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

/**
 * 建立標籤指令處理器
 * 
 * 遵循SOLID原則：
 * - SRP (單一責任原則): 專門處理標籤建立邏輯
 * - DIP (依賴反轉原則): 依賴TagManager抽象而非具體實作
 * - OCP (開放封閉原則): 實作CommandHandler介面，可擴展而不修改現有程式碼
 */
public class CreateTagCommandHandler implements CommandHandler {
    
    private final KoukeNekoNametag plugin;
    private final TagManager tagManager;
    
    public CreateTagCommandHandler(KoukeNekoNametag plugin, TagManager tagManager) {
        this.plugin = plugin;
        this.tagManager = tagManager;
    }
    
    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length < 3) {
            plugin.getMessageManager().sendConfigMessage(sender, "tag.create_usage");
            return true;
        }
        
        String tagId = args[1];
        String display = args[2];
        
        // SRP: 使用Tag的靜態方法驗證ID
        if (!Tag.isValidId(tagId)) {
            plugin.getMessageManager().sendConfigMessage(sender, "tag.invalid_id");
            return true;
        }
        
        // 檢查標籤是否已存在
        if (tagManager.getTag(tagId) != null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("tag", tagId);
            plugin.getMessageManager().sendConfigMessage(sender, "tag.tag_exists", placeholders);
            return true;
        }
        
        // SRP: 委派給TagManager處理標籤建立
        Tag tag = tagManager.createTag(tagId, display);
        if (tag != null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("display", tag.getDisplay());
            placeholders.put("id", tag.getId());
            plugin.getMessageManager().sendConfigMessage(sender, "tag.created", placeholders);
        } else {
            plugin.getMessageManager().sendConfigMessage(sender, "tag.create_failed");
        }
        
        return true;
    }
    
    @Override
    public String getCommandName() {
        return "create";
    }
    
    @Override
    public boolean canHandle(String[] args) {
        return args.length > 0 && "create".equalsIgnoreCase(args[0]);
    }
}