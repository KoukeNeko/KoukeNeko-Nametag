package dev.doeshing.koukeNekoNametag.core.tag;

/**
 * 代表一個標籤
 */
public class Tag {
    private final String id;          // 標籤唯一識別碼
    private final String display;     // 顯示格式 (例如 "&7[&bBeta&7]&f")

    /**
     * 建立一個新的標籤
     * 
     * @param id 標籤ID
     * @param display 顯示格式
     */
    public Tag(String id, String display) {
        this.id = id;
        this.display = display;
    }

    public String getId() {
        return id;
    }

    public String getDisplay() {
        return display;
    }

    public String getPermission() {
        return "koukeneko.tags." + id;
    }
}