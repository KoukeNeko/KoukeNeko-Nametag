package dev.doeshing.koukeNekoNametag.core.tag;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * 標籤選擇選單管理器
 * 
 * 遵循SOLID原則重構：
 * - SRP (單一責任原則): 專門負責選單的建立和事件處理
 * - DIP (依賴反轉原則): 依賴TagManager抽象而非具體實作
 * - OCP (開放封閉原則): 可擴展選單功能而不修改現有程式碼
 */
public class TagMenu implements Listener {
    private final KoukeNekoNametag plugin;
    private final TagManager tagManager;
    private final Map<UUID, Inventory> openMenus = new HashMap<>(); // SRP: 專門管理開啟的選單
    private final Map<UUID, List<Tag>> menuTags = new HashMap<>();   // SRP: 專門管理選單標籤映射

    /**
     * 建立標籤選單管理器
     * 
     * DIP原則: 依賴TagManager抽象而非具體實作
     */
    public TagMenu(KoukeNekoNametag plugin, TagManager tagManager) {
        this.plugin = plugin;
        this.tagManager = tagManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * 為玩家打開標籤選擇選單
     * SRP: 協調選單建立的整個流程
     */
    public void openMenu(Player player) {
        // SRP: 委派給TagManager獲取可用標籤
        List<Tag> availableTags = tagManager.getAvailableTags(player);
        
        if (availableTags.isEmpty()) {
            plugin.getMessageManager().sendConfigMessage(player, "menu.no_tags");
            return;
        }

        // SRP: 委派給專門的方法建立選單
        Inventory menu = createMenuInventory(availableTags);
        
        // SRP: 委派給專門的方法填充選單
        populateMenu(menu, availableTags, player);
        addRemoveButton(menu);

        // SRP: 委派給專門的方法註冊選單
        registerMenu(player, menu, availableTags);
        
        // 打開選單
        player.openInventory(menu);
    }
    
    /**
     * 建立選單定器
     * SRP: 專門負責選單定器的建立
     */
    private Inventory createMenuInventory(List<Tag> availableTags) {
        int rows = Math.min(6, (availableTags.size() + 8) / 9 + 1);
        Component title = LegacyComponentSerializer.legacyAmpersand().deserialize(
                plugin.getMessageManager().getMessage("menu.title"));
        return Bukkit.createInventory(null, rows * 9, title);
    }
    
    /**
     * 新增移除按鈕
     * SRP: 專門負責移除按鈕的建立
     */
    private void addRemoveButton(Inventory menu) {
        ItemStack removeItem = new ItemStack(Material.BARRIER);
        ItemMeta removeMeta = removeItem.getItemMeta();
        removeMeta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(
                plugin.getMessageManager().getMessage("menu.remove_button")));
        removeItem.setItemMeta(removeMeta);
        menu.setItem(menu.getSize() - 1, removeItem);
    }
    
    /**
     * 註冊選單
     * SRP: 專門負責選單的註冊和狀態管理
     */
    private void registerMenu(Player player, Inventory menu, List<Tag> availableTags) {
        openMenus.put(player.getUniqueId(), menu);
        menuTags.put(player.getUniqueId(), availableTags);
    }

    /**
     * 填充選單物品
     * SRP: 專門負責選單物品的建立和設定
     */
    private void populateMenu(Inventory menu, List<Tag> tags, Player player) {
        for (int i = 0; i < tags.size(); i++) {
            Tag tag = tags.get(i);
            ItemStack item = createTagMenuItem(tag, player);
            menu.setItem(i, item);
        }
    }
    
    /**
     * 建立標籤選單物品
     * SRP: 專門負責單一標籤物品的建立
     */
    private ItemStack createTagMenuItem(Tag tag, Player player) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        
        // 設定項目名稱
        meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(tag.getDisplay()));
        
        // SRP: 委派給專門的方法建立描述
        meta.lore(createTagItemLore(tag, player));
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * 建立標籤物品的描述文字
     * SRP: 專門負責物品描述的建立
     */
    private List<Component> createTagItemLore(Tag tag, Player player) {
        List<Component> lore = new ArrayList<>();
        
        // 新增選擇提示
        lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(
                plugin.getMessageManager().getMessage("menu.select_tag")));
        
        // 新增標籤ID資訊
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("id", tag.getId());
        lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(
                plugin.getMessageManager().getMessage("menu.tag_id", placeholders)));

        // 為管理員新增權限資訊
        if (hasAdminPermission(player)) {
            placeholders.clear();
            placeholders.put("permission", tag.getPermission());
            lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(
                    plugin.getMessageManager().getMessage("menu.tag_permission", placeholders)));
        }
        
        return lore;
    }
    
    /**
     * 檢查是否為管理員
     * SRP: 專門負責權限檢查
     */
    private boolean hasAdminPermission(Player player) {
        return player.hasPermission("koukeneko.admin");
    }

    /**
     * 處理選單點選事件
     * SRP: 專門負責選單事件的處理和路由
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID playerId = player.getUniqueId();
        
        // SRP: 委派給專門的方法檢查是否為我們管理的選單
        if (!isOurMenu(playerId, event.getInventory())) {
            return;
        }
        
        event.setCancelled(true);
        
        int slot = event.getSlot();
        if (slot < 0) {
            return;
        }
        
        Inventory openMenu = openMenus.get(playerId);
        
        // SRP: 委派給專門的方法處理移除按鈕點選
        if (isRemoveButtonClick(slot, openMenu, event.getCurrentItem())) {
            handleRemoveButtonClick(player);
            return;
        }
        
        // SRP: 委派給專門的方法處理標籤選擇
        handleTagSelection(player, playerId, slot);
    }
    
    /**
     * 檢查是否為我們管理的選單
     * SRP: 專門負責選單驗證
     */
    private boolean isOurMenu(UUID playerId, Inventory clickedInventory) {
        if (!openMenus.containsKey(playerId)) {
            return false;
        }
        
        Inventory openMenu = openMenus.get(playerId);
        return clickedInventory == openMenu;
    }
    
    /**
     * 檢查是否為移除按鈕點選
     * SRP: 專門負責移除按鈕的識別
     */
    private boolean isRemoveButtonClick(int slot, Inventory menu, ItemStack clickedItem) {
        return slot == menu.getSize() - 1 && 
               clickedItem != null && 
               clickedItem.getType() == Material.BARRIER;
    }
    
    /**
     * 處理移除按鈕點選
     * SRP: 專門負責移除標籤操作
     */
    private void handleRemoveButtonClick(Player player) {
        tagManager.removeActiveTag(player);
        plugin.getMessageManager().sendConfigMessage(player, "menu.tag_removed");
        player.closeInventory();
    }
    
    /**
     * 處理標籤選擇
     * SRP: 專門負責標籤選擇操作
     */
    private void handleTagSelection(Player player, UUID playerId, int slot) {
        List<Tag> tags = menuTags.get(playerId);
        if (slot >= tags.size()) {
            return;
        }
        
        Tag selectedTag = tags.get(slot);
        
        // SRP: 委派給TagManager處理標籤設定
        if (tagManager.setActiveTag(player, selectedTag)) {
            sendTagSetSuccessMessage(player, selectedTag);
        } else {
            plugin.getMessageManager().sendConfigMessage(player, "menu.tag_set_failed");
        }
        
        player.closeInventory();
    }
    
    /**
     * 傳送標籤設定成功訊息
     * SRP: 專門負責成功訊息的傳送
     */
    private void sendTagSetSuccessMessage(Player player, Tag selectedTag) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("display", selectedTag.getDisplay());
        plugin.getMessageManager().sendConfigMessage(player, "menu.tag_set", placeholders);
    }

    /**
     * 處理選單關閉事件
     * SRP: 專門負責清理選單狀態
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        cleanupPlayerMenu(player.getUniqueId());
    }
    
    /**
     * 清理玩家選單狀態
     * SRP: 專門負責選單資源的清理
     */
    private void cleanupPlayerMenu(UUID playerId) {
        openMenus.remove(playerId);
        menuTags.remove(playerId);
    }
}