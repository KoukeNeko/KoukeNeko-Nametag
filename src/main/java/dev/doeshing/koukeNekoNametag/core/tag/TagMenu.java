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
 * 標籤選擇菜單
 */
public class TagMenu implements Listener {
    private final KoukeNekoNametag plugin;
    private final TagManager tagManager;
    private final Map<UUID, Inventory> openMenus = new HashMap<>();
    private final Map<UUID, List<Tag>> menuTags = new HashMap<>();

    public TagMenu(KoukeNekoNametag plugin, TagManager tagManager) {
        this.plugin = plugin;
        this.tagManager = tagManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * 為玩家打開標籤選擇選單
     */
    public void openMenu(Player player) {
        // 獲取玩家可用的標籤
        List<Tag> availableTags = tagManager.getAvailableTags(player);
        if (availableTags.isEmpty()) {
            plugin.getMessageManager().sendMessage(player, "&c你沒有可用的標籤!");
            return;
        }

        // 建立菜單
        int rows = Math.min(6, (availableTags.size() + 8) / 9 + 1); // 計算需要的行數
        Component title = LegacyComponentSerializer.legacyAmpersand().deserialize("&8標籤選擇");
        Inventory menu = Bukkit.createInventory(null, rows * 9, title);

        // 填充菜單
        populateMenu(menu, availableTags, player);

        // 新增移除標籤的選項
        ItemStack removeItem = new ItemStack(Material.BARRIER);
        ItemMeta removeMeta = removeItem.getItemMeta();
        removeMeta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("&c移除目前標籤"));
        removeItem.setItemMeta(removeMeta);
        menu.setItem(rows * 9 - 1, removeItem);

        // 保存菜單和標籤列表
        openMenus.put(player.getUniqueId(), menu);
        menuTags.put(player.getUniqueId(), availableTags);

        // 打開菜單
        player.openInventory(menu);
    }

    /**
     * 填充菜單物品
     */
    private void populateMenu(Inventory menu, List<Tag> tags, Player player) {
        for (int i = 0; i < tags.size(); i++) {
            Tag tag = tags.get(i);
            
            // 所有標籤都使用相同的材質
            ItemStack item = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = item.getItemMeta();
            
            // 設置名稱和描述
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(tag.getDisplay()));
            
            List<Component> lore = new ArrayList<>();
            lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize("&7點擊選擇此標籤"));
            lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize("&7ID: &f" + tag.getId()));

            // 檢查管理員權限
            if (player.hasPermission("koukeneko.admin")) {
                lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize("&7權限: &f" + tag.getPermission()));
            }
            
            meta.lore(lore);
            item.setItemMeta(meta);
            
            menu.setItem(i, item);
        }
    }

    /**
     * 處理菜單點擊事件
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID playerId = player.getUniqueId();
        
        if (!openMenus.containsKey(playerId)) {
            return;
        }
        
        Inventory openMenu = openMenus.get(playerId);
        if (event.getInventory() != openMenu) {
            return;
        }
        
        event.setCancelled(true);
        
        int slot = event.getSlot();
        if (slot < 0) {
            return;
        }
        
        // 檢查是否點擊了移除標籤的選項
        if (slot == openMenu.getSize() - 1 && event.getCurrentItem() != null && 
                event.getCurrentItem().getType() == Material.BARRIER) {
            tagManager.removeActiveTag(player);
            plugin.getMessageManager().sendMessage(player, "&a已移除標籤!");
            player.closeInventory();
            return;
        }
        
        // 獲取對應的標籤
        List<Tag> tags = menuTags.get(playerId);
        if (slot >= tags.size()) {
            return;
        }
        
        Tag selectedTag = tags.get(slot);
        
        // 設置活躍標籤
        if (tagManager.setActiveTag(player, selectedTag)) {
            plugin.getMessageManager().sendMessage(player, "&a已設定標籤: " + selectedTag.getDisplay());
        } else {
            plugin.getMessageManager().sendMessage(player, "&c無法設定標籤!");
        }
        
        player.closeInventory();
    }

    /**
     * 處理選單關閉事件
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        openMenus.remove(player.getUniqueId());
        menuTags.remove(player.getUniqueId());
    }
}