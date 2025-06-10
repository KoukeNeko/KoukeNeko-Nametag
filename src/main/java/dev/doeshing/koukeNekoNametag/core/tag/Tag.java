package dev.doeshing.koukeNekoNametag.core.tag;

import java.util.Objects;

/**
 * 代表一個標籤的值物件 (Value Object)
 * 
 * 遵循SOLID原則重構：
 * - SRP (單一責任原則): 只負責封裝標籤資料，不處理業務邏輯
 * - LSP (里氏替換原則): 作為不變物件，保證行為一致性
 * - 不變性 (Immutability): 作為值物件，所有屬性都是不可變的
 * - 封裝性 (Encapsulation): 適當封裝內部狀態和權限計算邏輯
 */
public final class Tag {
    private final String id;               // 標籤唯一識別碼 (不可變)
    private final String display;          // 顯示格式 (不可變)
    private final String permissionPrefix; // 權限前綴 (不可變)

    /**
     * 建立一個新的標籤值物件
     * 
     * SRP: 單純的建構子，只負責初始化資料
     * 
     * @param id 標籤ID (不能為 null 或空字串)
     * @param display 顯示格式 (不能為 null)
     * @param permissionPrefix 權限前綴 (不能為 null)
     * @throws IllegalArgumentException 當參數無效時
     */
    public Tag(String id, String display, String permissionPrefix) {
        // 輸入驗證 - 保證物件的有效性
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag ID cannot be null or empty");
        }
        if (display == null) {
            throw new IllegalArgumentException("Tag display cannot be null");
        }
        if (permissionPrefix == null) {
            throw new IllegalArgumentException("Permission prefix cannot be null");
        }
        
        this.id = id.trim();
        this.display = display;
        this.permissionPrefix = permissionPrefix;
    }

    /**
     * 獲取標籤ID
     * SRP: 單純的 getter 方法
     */
    public String getId() {
        return id;
    }
    
    /**
     * 獲取顯示格式
     * SRP: 單純的 getter 方法
     */
    public String getDisplay() {
        return display;
    }
    
    /**
     * 獲取完整的權限字串
     * SRP: 封裝權限計算邏輯於物件內部
     * 
     * @return 完整的權限字串
     */
    public String getPermission() {
        return permissionPrefix + id;
    }
    
    /**
     * 檢查是否為有效的標籤ID
     * SRP: 封裝驗證邏輯
     * 
     * @param tagId 要檢查的標籤ID
     * @return 是否為有效ID
     */
    public static boolean isValidId(String tagId) {
        return tagId != null && !tagId.trim().isEmpty() && tagId.matches("[a-zA-Z0-9_-]+");
    }
    
    /**
     * 物件相等性檢查
     * 作為值物件，必須正確實作 equals 和 hashCode
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Tag tag = (Tag) obj;
        return Objects.equals(id, tag.id) &&
               Objects.equals(display, tag.display) &&
               Objects.equals(permissionPrefix, tag.permissionPrefix);
    }
    
    /**
     * 物件雜湊碼
     * 作為值物件，必須正確實作 hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, display, permissionPrefix);
    }
    
    /**
     * 字串表示法
     * 方便除錯和日誌輸出
     */
    @Override
    public String toString() {
        return String.format("Tag{id='%s', display='%s', permission='%s'}", 
                           id, display, getPermission());
    }
}