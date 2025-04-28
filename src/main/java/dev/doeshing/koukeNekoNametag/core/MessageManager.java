package dev.doeshing.koukeNekoNametag.core;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import dev.doeshing.koukeNekoNametag.core.lang.LanguageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class MessageManager {

    private final KoukeNekoNametag plugin;
    private String prefix;

    public MessageManager(KoukeNekoNametag plugin) {
        this.plugin = plugin;
        loadPrefix();
    }

    /**
     * 從設定檔讀取前綴
     */
    public void loadPrefix() {
        this.prefix = plugin.getConfig().getString("prefix", "&7[&b&l🕹️&7]&f");
    }

    /**
     * 將帶有顏色代碼的字串轉換為 Component
     * @param text 帶有顏色代碼的字串
     * @return Component 物件
     */
    public Component format(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    /**
     * 傳送訊息給指定的接收者
     * @param receiver 訊息接收者
     * @param message 訊息內容
     */
    public void sendMessage(CommandSender receiver, String message) {
        receiver.sendMessage(format(prefix + " " + message));
    }

    /**
     * 廣播訊息給所有玩家
     * @param message 訊息內容
     */
    public void broadcastMessage(String message) {
        Bukkit.broadcast(format(prefix + " " + message));
    }

    /**
     * 廣播訊息給有特定權限的玩家
     * @param message 訊息內容
     * @param permission 所需權限
     */
    public void broadcastMessage(String message, String permission) {
        Component component = format(prefix + " " + message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                player.sendMessage(component);
            }
        }
        // 也發送給控制台
        Bukkit.getConsoleSender().sendMessage(component);
    }

    /**
     * 取得語言檔中的訊息
     * @param path 訊息路徑
     * @return 格式化後的訊息字串
     */
    public String getMessage(String path) {
        return plugin.getLanguageManager().getMessage(path);
    }

    /**
     * 取得語言檔中的訊息，並替換佔位符
     * @param path 訊息路徑
     * @param placeholders 佔位符和值的映射
     * @return 替換後的訊息字串
     */
    public String getMessage(String path, Map<String, String> placeholders) {
        return plugin.getLanguageManager().getMessage(path, placeholders);
    }

    /**
     * 傳送語言檔中的訊息給指定的接收者
     * @param receiver 訊息接收者
     * @param path 訊息路徑
     */
    public void sendConfigMessage(CommandSender receiver, String path) {
        sendMessage(receiver, getMessage(path));
    }
    
    /**
     * 傳送語言檔中的訊息給指定的接收者，並替換佔位符
     * @param receiver 訊息接收者
     * @param path 訊息路徑
     * @param placeholders 佔位符和值的映射
     */
    public void sendConfigMessage(CommandSender receiver, String path, Map<String, String> placeholders) {
        sendMessage(receiver, getMessage(path, placeholders));
    }
}
