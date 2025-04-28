package dev.doeshing.koukeNekoNametag.core.tag;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 管理所有標籤
 */
public class TagManager {
    private final KoukeNekoNametag plugin;
    private final Map<String, Tag> tags; // 標籤ID到標籤對象的映射
    private File tagsFile;
    private FileConfiguration tagsConfig;

    public TagManager(KoukeNekoNametag plugin) {
        this.plugin = plugin;
        this.tags = new HashMap<>();
        loadTagsConfig();
    }

    /**
     * 加載標籤設定
     */
    public void loadTagsConfig() {
        tagsFile = new File(plugin.getDataFolder(), "tags.yml");
        if (!tagsFile.exists()) {
            plugin.saveResource("tags.yml", false);
        }
        tagsConfig = YamlConfiguration.loadConfiguration(tagsFile);
        loadTags();
    }

    /**
     * 從設定中加載所有標籤
     */
    private void loadTags() {
        tags.clear();
        ConfigurationSection tagsSection = tagsConfig.getConfigurationSection("tags");
        if (tagsSection == null) {
            plugin.getLogger().warning("No tags section found in tags.yml!");
            return;
        }

        for (String tagId : tagsSection.getKeys(false)) {
            String display = tagsSection.getString(tagId + ".display", "&7[" + tagId + "]&f");
            
            Tag tag = new Tag(tagId, display);
            tags.put(tagId, tag);
            plugin.getLogger().info("Loaded tag: " + tagId + " with display: " + display);
        }
    }

    /**
     * 保存標籤設定
     */
    public void saveTags() {
        // 保存標籤數據
        for (Map.Entry<String, Tag> entry : tags.entrySet()) {
            String tagId = entry.getKey();
            Tag tag = entry.getValue();
            
            tagsConfig.set("tags." + tagId + ".display", tag.getDisplay());
        }
        
        try {
            tagsConfig.save(tagsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save tags.yml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 重新加載標籤設定
     */
    public void reload() {
        loadTagsConfig();
    }

    /**
     * 獲取所有標籤
     */
    public Collection<Tag> getAllTags() {
        return tags.values();
    }

    /**
     * 根據ID獲取標籤
     */
    public Tag getTag(String id) {
        return tags.get(id);
    }

    /**
     * 建立新標籤
     */
    public Tag createTag(String id, String display) {
        Tag tag = new Tag(id, display);
        tags.put(id, tag);
        
        // 保存到設定
        tagsConfig.set("tags." + id + ".display", display);
        
        try {
            tagsConfig.save(tagsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save tags.yml: " + e.getMessage());
            e.printStackTrace();
        }
        
        return tag;
    }

    /**
     * 刪除標籤
     */
    public boolean deleteTag(String id) {
        if (!tags.containsKey(id)) {
            return false;
        }
        
        tags.remove(id);
        tagsConfig.set("tags." + id, null);
        
        try {
            tagsConfig.save(tagsFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save tags.yml: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 獲取玩家可用的所有標籤
     */
    public List<Tag> getAvailableTags(Player player) {
        List<Tag> availableTags = new ArrayList<>();
        
        for (Tag tag : tags.values()) {
            // 檢查是否有相關權限
            if (player.hasPermission(tag.getPermission())) {
                availableTags.add(tag);
            }
        }
        
        return availableTags;
    }

    /**
     * 設定玩家的啟用標籤
     */
    public boolean setActiveTag(Player player, Tag tag) {
        if (tag == null) {
            return false;
        }
        
        // 檢查玩家是否有權限使用此標籤
        if (!player.hasPermission(tag.getPermission())) {
            return false;
        }
        
        // 先移除現有標籤
        removeActiveTag(player);
        
        // 執行設定標籤的指令
        List<String> commands = plugin.getConfig().getStringList("command.settag");
        for (String cmd : commands) {
            cmd = cmd.replace("{player}", player.getName())
                    .replace("{tag}", tag.getDisplay());
            plugin.getLogger().info("執行指令: " + cmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        
        return true;
    }

    /**
     * 移除玩家的啟用標籤
     */
    public boolean removeActiveTag(Player player) {
        // 執行移除標籤的指令
        List<String> commands = plugin.getConfig().getStringList("command.remove");
        for (String cmd : commands) {
            cmd = cmd.replace("{player}", player.getName());
            plugin.getLogger().info("執行指令: " + cmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        
        return true;
    }

    /**
     * 給予玩家標籤權限
     */
    public boolean giveTagPermission(Player player, Tag tag) {
        if (tag == null) {
            return false;
        }
        
        // 執行新增權限的指令
        List<String> commands = plugin.getConfig().getStringList("command.add_permission");
        for (String cmd : commands) {
            cmd = cmd.replace("{player}", player.getName())
                    .replace("{tag}", tag.getPermission());
            plugin.getLogger().info("執行指令: " + cmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        
        return true;
    }

    /**
     * 移除玩家標籤權限
     */
    public boolean removeTagPermission(Player player, Tag tag) {
        if (tag == null) {
            return false;
        }
        
        // 執行移除權限的指令
        List<String> commands = plugin.getConfig().getStringList("command.remove_permission");
        for (String cmd : commands) {
            cmd = cmd.replace("{player}", player.getName())
                    .replace("{tag}", tag.getPermission());
            plugin.getLogger().info("執行指令: " + cmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        
        return true;
    }
}