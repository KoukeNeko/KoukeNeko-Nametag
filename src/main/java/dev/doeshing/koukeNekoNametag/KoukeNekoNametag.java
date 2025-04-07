package dev.doeshing.koukeNekoNametag;

import dev.doeshing.koukeNekoNametag.commands.ReloadCommand;
import dev.doeshing.koukeNekoNametag.commands.TagCommand;
import dev.doeshing.koukeNekoNametag.core.CommandSystem;
import dev.doeshing.koukeNekoNametag.core.MessageManager;
import dev.doeshing.koukeNekoNametag.core.tag.TagManager;
import dev.doeshing.koukeNekoNametag.core.tag.TagMenu;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class KoukeNekoNametag extends JavaPlugin {

    private MessageManager messageManager;
    private CommandSystem commandSystem;
    private TagManager tagManager;
    private TagMenu tagMenu;

    @Override
    public void onEnable() {
        // 初始化配置
        saveDefaultConfig();
        
        // 初始化消息管理器
        this.messageManager = new MessageManager(this);
        
        // 初始化命令系統
        this.commandSystem = new CommandSystem(this);
        
        // 初始化標籤管理器
        this.tagManager = new TagManager(this);
        
        // 初始化標籤選單
        this.tagMenu = new TagMenu(this, tagManager);
        
        // 註冊命令
        registerCommands();
        
        getLogger().info("KoukeNeko 標籤系統已啟用！");
    }

    @Override
    public void onDisable() {
        // 保存標籤數據
        if (tagManager != null) {
            tagManager.saveTags();
        }
        
        getLogger().info("KoukeNeko 標籤系統已停用！");
    }

    /**
     * 註冊所有命令
     */
    private void registerCommands() {
        // 註冊 /koukeneko reload 命令
        commandSystem.registerCommand(
                "koukeneko",
                new ReloadCommand(this),
                "koukeneko.admin",
                "KoukeNeko 插件主命令",
                "/koukeneko reload",
                "kn"
        );
        
        // 註冊 /tag 命令
        TagCommand tagCommand = new TagCommand(this, tagManager, tagMenu);
        commandSystem.registerCommand(
                "tag",
                tagCommand,
                null, // 不需要權限，具體權限檢查在命令處理中
                "標籤系統命令",
                "/tag [參數]",
                "標籤", "tags"
        );
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public TagManager getTagManager() {
        return tagManager;
    }

    /**
     * 重新載入插件配置
     */
    @Override
    public void reloadConfig() {
        try {
            super.reloadConfig();

            // 重新載入相關配置
            if (messageManager != null) {
                messageManager.loadPrefix();
                getLogger().info("訊息前綴已重新載入");
            }
            
            // 重新載入標籤設定
            if (tagManager != null) {
                tagManager.reload();
                getLogger().info("標籤設定已重新載入");
            }

        } catch (Exception e) {
            getLogger().severe("重新載入設定檔時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
}