package dev.doeshing.koukeNekoNametag;

import dev.doeshing.koukeNekoNametag.core.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class KoukeNekoNametag extends JavaPlugin {

    private MessageManager messageManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    /**
     * 重新載入插件配置
     */
    @Override
    public void reloadConfig() {
        try {
            super.reloadConfig();

            // 重新載入相關配置
            if (messageManager != null) {
                messageManager.loadPrefix();
                getLogger().info("訊息前綴已重新載入");
            }

        } catch (Exception e) {
            getLogger().severe("重新載入設定檔時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
