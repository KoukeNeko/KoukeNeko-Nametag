package dev.doeshing.koukeNekoNametag.core.lang;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 語言管理器 - 處理多語言支持
 */
public class LanguageManager {
    private final KoukeNekoNametag plugin;
    private FileConfiguration langConfig;
    private String language;
    private File langFile;
    
    public LanguageManager(KoukeNekoNametag plugin) {
        this.plugin = plugin;
        this.language = plugin.getConfig().getString("language", "zh_TW");
        loadLanguage();
    }
    
    /**
     * 載入語言文件
     */
    private void loadLanguage() {
        langFile = new File(plugin.getDataFolder(), "lang_" + language + ".yml");
        
        // 如果語言文件不存在，嘗試從資源目錄儲存
        if (!langFile.exists()) {
            try {
                InputStream defaultLangStream = plugin.getResource("lang_" + language + ".yml");
                if (defaultLangStream != null) {
                    plugin.saveResource("lang_" + language + ".yml", false);
                } else {
                    // 如果請求的語言文件不存在，使用默認語言
                    plugin.getLogger().warning("找不到語言檔: " + language + "，使用默認語言");
                    language = "zh_TW";
                    langFile = new File(plugin.getDataFolder(), "lang_" + language + ".yml");
                    plugin.saveResource("lang_" + language + ".yml", false);
                }
            } catch (Exception e) {
                plugin.getLogger().severe("無法儲存語言檔: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // 載入語言文件
        langConfig = YamlConfiguration.loadConfiguration(langFile);
        
        // 檢查是否需要更新語言文件
        checkForMissingMessages();
    }
    
    /**
     * 檢查語言文件中是否缺少訊息，如果缺少則更新
     */
    private void checkForMissingMessages() {
        // 獲取默認的語言文件
        InputStream defaultLangStream = plugin.getResource("lang_" + language + ".yml");
        if (defaultLangStream == null) {
            return;
        }
        
        // 載入默認語言文件
        YamlConfiguration defaultLangConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(defaultLangStream, StandardCharsets.UTF_8));
        
        // 檢查缺失的訊息
        boolean updated = false;
        for (String key : defaultLangConfig.getKeys(true)) {
            if (!defaultLangConfig.isConfigurationSection(key) && !langConfig.contains(key)) {
                langConfig.set(key, defaultLangConfig.get(key));
                updated = true;
            }
        }
        
        // 如果有更新，儲存文件
        if (updated) {
            try {
                langConfig.save(langFile);
                plugin.getLogger().info("語言檔已更新: " + langFile.getName());
            } catch (IOException e) {
                plugin.getLogger().severe("無法儲存更新的語言檔: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 獲取訊息並替換佔位符
     * 
     * @param path 訊息路徑
     * @param placeholders 佔位符和值的映射
     * @return 替換後的訊息
     */
    public String getMessage(String path, Map<String, String> placeholders) {
        String message = langConfig.getString(path);
        
        // 如果找不到指定路徑的訊息，返回錯誤訊息
        if (message == null) {
            return "&c找不到訊息: " + path;
        }
        
        // 替換佔位符
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        
        return message;
    }
    
    /**
     * 獲取不需要替換的訊息
     * 
     * @param path 訊息路徑
     * @return 訊息內容
     */
    public String getMessage(String path) {
        return getMessage(path, null);
    }
    
    /**
     * 創建包含單個佔位符的替換映射
     * 
     * @param key 佔位符名稱
     * @param value 替換值
     * @return 佔位符映射
     */
    public static Map<String, String> placeholder(String key, String value) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(key, value);
        return placeholders;
    }
    
    /**
     * 創建包含多個佔位符的替換映射
     * 
     * @param keys 佔位符名稱數組
     * @param values 替換值數組
     * @return 佔位符映射
     */
    public static Map<String, String> placeholders(String[] keys, String[] values) {
        if (keys.length != values.length) {
            throw new IllegalArgumentException("佔位符鍵和值的數量必須相同");
        }
        
        Map<String, String> placeholders = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            placeholders.put(keys[i], values[i]);
        }
        
        return placeholders;
    }
    
    /**
     * 重新載入語言設定
     */
    public void reload() {
        this.language = plugin.getConfig().getString("language", "zh_TW");
        loadLanguage();
    }
}
