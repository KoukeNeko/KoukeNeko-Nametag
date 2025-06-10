package dev.doeshing.koukeNekoNametag.core.tag;

import org.bukkit.entity.Player;

/**
 * 標籤顯示操作服務介面
 * 
 * 遵循SOLID原則：
 * - SRP (單一責任原則): 專門負責標籤顯示相關操作
 * - DIP (依賴反轉原則): 定義抽象介面，不依賴具體實作
 * - ISP (介面隔離原則): 提供專門的標籤顯示操作介面
 */
public interface TagDisplayService {
    
    /**
     * 設定玩家的啟用標籤
     * @param player 目標玩家
     * @param tag 要設定的標籤
     * @return 操作是否成功
     */
    boolean setActiveTag(Player player, Tag tag);
    
    /**
     * 移除玩家的啟用標籤
     * @param player 目標玩家
     * @return 操作是否成功
     */
    boolean removeActiveTag(Player player);
}