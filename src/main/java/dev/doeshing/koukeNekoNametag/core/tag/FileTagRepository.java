package dev.doeshing.koukeNekoNametag.core.tag;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 基於檔案的標籤資料儲存實作
 * 
 * 遵循SOLID原則：
 * - SRP (單一責任原則): 專門負責檔案資料存取
 * - DIP (依賴反轉原則): 實作TagRepository介面
 * - OCP (開放封閉原則): 透過介面擴展，對修改封閉
 */
public class FileTagRepository implements TagRepository {
    
    private final KoukeNekoNametag plugin;
    private final Map<String, Tag> tags;
    private File tagsFile;
    private FileConfiguration tagsConfig;
    private String permissionPrefix;
    
    /**
     * 建立檔案標籤儲存庫
     * @param plugin 外掛實例
     */
    public FileTagRepository(KoukeNekoNametag plugin) {
        this.plugin = plugin;
        this.tags = new HashMap<>();
        this.permissionPrefix = plugin.getConfig().getString("permission.tag_prefix", "koukeneko.tags.");
        initializeFile();
        loadAllTags();
    }
    
    /**
     * 初始化設定檔案
     * 遵循SRP: 專門負責檔案初始化
     */
    private void initializeFile() {
        String tagsFileName = plugin.getConfig().getString("files.tags_file", "tags.yml");
        tagsFile = new File(plugin.getDataFolder(), tagsFileName);
        if (!tagsFile.exists()) {
            plugin.saveResource(tagsFileName, false);
        }
        tagsConfig = YamlConfiguration.loadConfiguration(tagsFile);
    }
    
    @Override
    public Collection<Tag> loadAllTags() {
        tags.clear();
        ConfigurationSection tagsSection = tagsConfig.getConfigurationSection("tags");
        if (tagsSection == null) {
            plugin.getLogger().warning("No tags section found in tags.yml!");
            return new ArrayList<>();
        }
        
        // SRP: 專門處理標籤載入邏輯
        for (String tagId : tagsSection.getKeys(false)) {
            String display = tagsSection.getString(tagId + ".display", "&7[" + tagId + "]&f");
            Tag tag = new Tag(tagId, display, permissionPrefix);
            tags.put(tagId, tag);
            
            // 除錯日誌
            if (plugin.getConfig().getBoolean("debug.enabled", false)) {
                plugin.getLogger().info("Loaded tag: " + tagId + " with display: " + display);
            }
        }
        
        return new ArrayList<>(tags.values());
    }
    
    @Override
    public Optional<Tag> findById(String id) {
        return Optional.ofNullable(tags.get(id));
    }
    
    @Override
    public boolean save(Tag tag) {
        try {
            // SRP: 專門處理標籤儲存
            tags.put(tag.getId(), tag);
            tagsConfig.set("tags." + tag.getId() + ".display", tag.getDisplay());
            tagsConfig.save(tagsFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save tag: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean delete(String id) {
        if (!tags.containsKey(id)) {
            return false;
        }
        
        try {
            // SRP: 專門處理標籤刪除
            tags.remove(id);
            tagsConfig.set("tags." + id, null);
            tagsConfig.save(tagsFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to delete tag: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public void reload() {
        // SRP: 專門處理重新載入
        this.permissionPrefix = plugin.getConfig().getString("permission.tag_prefix", "koukeneko.tags.");
        initializeFile();
        loadAllTags();
    }
}