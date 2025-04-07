package dev.doeshing.koukeNekoNametag;

import dev.doeshing.koukeNekoNametag.core.MessageManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class KoukeNekoNametag extends JavaPlugin {

    private MessageManager messageManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms api = provider.getProvider();
            getLogger().info("LuckPerms API 已成功註冊");
            getLogger().info("LuckPerms API 版本: " + api.getPluginMetadata().getApiVersion());
        }

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
