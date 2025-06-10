package dev.doeshing.koukeNekoNametag.core.tag;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * 標籤管理服務
 * 
 * 遵循SOLID原則重構：
 * - SRP (單一責任原則): 專門負責標籤業務邏輯協調，不直接處理資料存取或權限操作
 * - DIP (依賴反轉原則): 依賴抽象介面而非具體實作
 * - OCP (開放封閉原則): 透過依賴注入支援不同實作方式
 * - LSP (里氏替換原則): 可以替換不同的服務實作而不影響行為
 * - ISP (介面隔離原則): 依賴專門的服務介面而非龐大的介面
 */
public class TagManager {
    private final KoukeNekoNametag plugin;
    private final TagRepository tagRepository;          // DIP: 依賴抽象而非具體實作
    private final TagPermissionService permissionService; // SRP: 分離權限管理責任
    private final TagDisplayService displayService;      // SRP: 分離顯示邏輯責任

    /**
     * 建立標籤管理器
     * 
     * DIP原則: 透過建構子注入依賴，而非直接建立具體實作
     */
    public TagManager(KoukeNekoNametag plugin, 
                     TagRepository tagRepository,
                     TagPermissionService permissionService,
                     TagDisplayService displayService) {
        this.plugin = plugin;
        this.tagRepository = tagRepository;      // DIP: 注入抽象依賴
        this.permissionService = permissionService; // SRP: 分離職責
        this.displayService = displayService;    // SRP: 分離職責
    }

    /**
     * 重新載入標籤設定
     * SRP: 委派給專門的儲存庫處理
     */
    public void reload() {
        tagRepository.reload();
    }

    /**
     * 獲取所有標籤
     * SRP: 委派給儲存庫處理資料存取
     */
    public Collection<Tag> getAllTags() {
        return tagRepository.loadAllTags();
    }

    /**
     * 根據ID獲取標籤
     * SRP: 委派給儲存庫處理資料存取
     */
    public Tag getTag(String id) {
        return tagRepository.findById(id).orElse(null);
    }

    /**
     * 建立新標籤
     * SRP: 委派給儲存庫處理資料持久化
     */
    public Tag createTag(String id, String display) {
        String permissionPrefix = plugin.getConfig().getString("permission.tag_prefix", "koukeneko.tags.");
        Tag tag = new Tag(id, display, permissionPrefix);
        
        if (tagRepository.save(tag)) {
            return tag;
        }
        return null;
    }

    /**
     * 刪除標籤
     * SRP: 委派給儲存庫處理資料刪除
     */
    public boolean deleteTag(String id) {
        return tagRepository.delete(id);
    }

    /**
     * 獲取玩家可用的所有標籤
     * SRP: 委派給權限服務檢查權限
     */
    public List<Tag> getAvailableTags(Player player) {
        List<Tag> availableTags = new ArrayList<>();
        
        for (Tag tag : getAllTags()) {
            if (permissionService.hasPermission(player, tag)) {
                availableTags.add(tag);
            }
        }
        
        return availableTags;
    }

    /**
     * 設定玩家的啟用標籤
     * SRP: 分離權限檢查和標籤設定責任
     */
    public boolean setActiveTag(Player player, Tag tag) {
        if (tag == null) {
            return false;
        }
        
        // SRP: 委派給權限服務檢查權限
        if (!permissionService.hasPermission(player, tag)) {
            return false;
        }
        
        // 先移除現有標籤
        displayService.removeActiveTag(player);
        
        // SRP: 委派給顯示服務處理標籤設定
        return displayService.setActiveTag(player, tag);
    }

    /**
     * 移除玩家的啟用標籤
     * SRP: 委派給顯示服務處理
     */
    public boolean removeActiveTag(Player player) {
        return displayService.removeActiveTag(player);
    }

    /**
     * 給予玩家標籤權限
     * SRP: 委派給權限服務處理
     */
    public boolean giveTagPermission(Player player, Tag tag) {
        return permissionService.givePermission(player, tag);
    }

    /**
     * 移除玩家標籤權限
     * SRP: 委派給權限服務處理
     */
    public boolean removeTagPermission(Player player, Tag tag) {
        return permissionService.removePermission(player, tag);
    }
}