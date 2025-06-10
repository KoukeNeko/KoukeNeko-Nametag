package dev.doeshing.koukeNekoNametag;

import dev.doeshing.koukeNekoNametag.commands.ReloadCommand;
import dev.doeshing.koukeNekoNametag.commands.TagCommand;
import dev.doeshing.koukeNekoNametag.core.CommandSystem;
import dev.doeshing.koukeNekoNametag.core.MessageManager;
import dev.doeshing.koukeNekoNametag.core.lang.LanguageManager;
import dev.doeshing.koukeNekoNametag.core.tag.*;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * KoukeNeko 標籤外掛主類別
 * 
 * 遵循SOLID原則重構：
 * - SRP (單一責任原則): 只負責外掛的生命週期管理和依賴組裝
 * - DIP (依賴反轉原則): 使用依賴注入組裝各種服務
 * - OCP (開放封閉原則): 透過介面和工廠模式支援擴展
 */
public final class KoukeNekoNametag extends JavaPlugin {

    // 核心服務組件 - DIP: 依賴抽象介面
    private MessageManager messageManager;
    private CommandSystem commandSystem;
    private LanguageManager languageManager;
    
    // 標籤系統組件 - SRP: 分離不同責任
    private TagRepository tagRepository;
    private TagPermissionService tagPermissionService;
    private TagDisplayService tagDisplayService;
    private TagManager tagManager;
    private TagMenu tagMenu;

    @Override
    public void onEnable() {
        try {
            // SRP: 委派給專門的方法處理初始化
            initializeConfiguration();
            initializeCoreServices();
            initializeTagSystem();
            registerCommands();
            
            getLogger().info("KoukeNeko 標籤系統已啟用！");
        } catch (Exception e) {
            getLogger().severe("外掛啟動失敗: " + e.getMessage());
            getLogger().log(java.util.logging.Level.SEVERE, "異常堆疊追蹤: ", e); // 更強健的日誌記錄方式
            // 停用外掛以防止不一致狀態
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    /**
     * 初始化配置
     * SRP: 專門負責配置的初始化
     */
    private void initializeConfiguration() {
        saveDefaultConfig();
    }
    
    /**
     * 初始化核心服務
     * SRP: 專門負責核心服務組件的初始化
     */
    private void initializeCoreServices() {
        this.languageManager = new LanguageManager(this);
        this.messageManager = new MessageManager(this);
        this.commandSystem = new CommandSystem(this);
    }
    
    /**
     * 初始化標籤系統
     * SRP: 專門負責標籤系統組件的初始化和依賴注入
     * DIP: 注入具體實作而非在TagManager內部建立
     */
    private void initializeTagSystem() {
        // 建立具體實作物件 - DIP: 這裡是唯一依賴具體實作的地方
        this.tagRepository = new FileTagRepository(this);
        this.tagPermissionService = new CommandTagPermissionService(this);
        this.tagDisplayService = new CommandTagDisplayService(this);
        
        // 組裝 TagManager - DIP: 注入所有依賴
        this.tagManager = new TagManager(this, tagRepository, tagPermissionService, tagDisplayService);
        
        // 組裝 TagMenu
        this.tagMenu = new TagMenu(this, tagManager);
    }

    @Override
    public void onDisable() {
        try {
            // SRP: 委派給專門的方法處理清理工作
            cleanupResources();
            getLogger().info("KoukeNeko 標籤系統已停用！");
        } catch (Exception e) {
            getLogger().severe("外掛停用時發生錯誤: " + e.getMessage());
            getLogger().log(java.util.logging.Level.SEVERE, "異常堆疊追蹤: ", e); // 更強健的日誌記錄方式
        }
    }
    
    /**
     * 清理資源
     * SRP: 專門負責資源清理
     */
    private void cleanupResources() {
        // 注意: 由於重構後 TagManager 不再直接負責儲存，
        // 這裡如果需要的話可以直接呼叫 tagRepository.save() 方法
    }

    /**
     * 註冊所有指令
     * SRP: 專門負責指令的註冊
     */
    private void registerCommands() {
        registerReloadCommand();
        registerTagCommand();
    }
    
    /**
     * 註冊重載指令
     * SRP: 專門負責重載指令的註冊
     */
    private void registerReloadCommand() {
        commandSystem.registerCommand(
                "koukeneko",
                new ReloadCommand(this),
                "koukeneko.admin",
                "KoukeNeko 插件主指令",
                "/koukeneko reload",
                "kn"
        );
    }
    
    /**
     * 註冊標籤指令
     * SRP: 專門負責標籤指令的註冊
     */
    private void registerTagCommand() {
        TagCommand tagCommand = new TagCommand(this, tagManager, tagMenu);
        commandSystem.registerCommand(
                "tag",
                tagCommand,
                null, // 不需要權限，具體權限檢查在指令處理中
                "標籤系統指令",
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
    
    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    /**
     * 重新載入外掛設定
     * SRP: 專門負責協調所有組件的重載
     */
    @Override
    public void reloadConfig() {
        try {
            super.reloadConfig();
            
            // SRP: 委派給專門的方法處理各種重載
            reloadLanguageSettings();
            reloadMessageSettings();
            reloadTagSettings();
            
            getLogger().info("所有設定已重新載入");

        } catch (Exception e) {
            getLogger().severe("重新載入設定檔時發生錯誤: " + e.getMessage());
            getLogger().log(java.util.logging.Level.SEVERE, "異常堆疊追蹤: ", e); // 更強健的日誌記錄方式
        }
    }
    
    /**
     * 重載語言設定
     * SRP: 專門負責語言設定的重載
     */
    private void reloadLanguageSettings() {
        if (languageManager != null) {
            languageManager.reload();
            getLogger().info("語言設定已重新載入");
        }
    }
    
    /**
     * 重載訊息設定
     * SRP: 專門負責訊息設定的重載
     */
    private void reloadMessageSettings() {
        if (messageManager != null) {
            messageManager.loadPrefix();
            getLogger().info("訊息前綴已重新載入");
        }
    }
    
    /**
     * 重載標籤設定
     * SRP: 專門負責標籤設定的重載
     */
    private void reloadTagSettings() {
        if (tagManager != null) {
            tagManager.reload();
            getLogger().info("標籤設定已重新載入");
        }
    }
}