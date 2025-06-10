package dev.doeshing.koukeNekoNametag.core.tag;

import java.util.Collection;
import java.util.Optional;

/**
 * 標籤資料儲存抽象介面
 * 
 * 遵循SOLID原則：
 * - DIP (依賴反轉原則): 高層模組不應依賴具體實作，而是依賴抽象
 * - ISP (介面隔離原則): 提供專門的標籤資料操作介面
 */
public interface TagRepository {
    
    /**
     * 載入所有標籤
     * @return 所有標籤的集合
     */
    Collection<Tag> loadAllTags();
    
    /**
     * 根據ID查找標籤
     * @param id 標籤ID
     * @return 標籤物件，如果不存在則為空
     */
    Optional<Tag> findById(String id);
    
    /**
     * 儲存標籤
     * @param tag 要儲存的標籤
     * @return 儲存是否成功
     */
    boolean save(Tag tag);
    
    /**
     * 刪除標籤
     * @param id 標籤ID
     * @return 刪除是否成功
     */
    boolean delete(String id);
    
    /**
     * 重新載入標籤資料
     */
    void reload();
}