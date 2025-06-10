package dev.doeshing.koukeNekoNametag.core.tag;

import org.bukkit.entity.Player;

/**
 * 標籤權限操作服務介面
 * 
 * 遵循SOLID原則：
 * - SRP (單一責任原則): 專門負責權限相關操作
 * - DIP (依賴反轉原則): 定義抽象介面，不依賴具體實作
 * - ISP (介面隔離原則): 提供專門的權限操作介面
 */
public interface TagPermissionService {
    
    /**
     * 給予玩家標籤權限
     * @param player 目標玩家
     * @param tag 標籤
     * @return 操作是否成功
     */
    boolean givePermission(Player player, Tag tag);
    
    /**
     * 移除玩家標籤權限
     * @param player 目標玩家
     * @param tag 標籤
     * @return 操作是否成功
     */
    boolean removePermission(Player player, Tag tag);
    
    /**
     * 檢查玩家是否有標籤權限
     * @param player 目標玩家
     * @param tag 標籤
     * @return 是否有權限
     */
    boolean hasPermission(Player player, Tag tag);
}