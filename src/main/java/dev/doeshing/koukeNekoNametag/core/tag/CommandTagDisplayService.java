package dev.doeshing.koukeNekoNametag.core.tag;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * 基於指令的標籤顯示服務實作
 * 
 * 遵循SOLID原則：
 * - SRP (單一責任原則): 專門負責標籤顯示相關指令執行
 * - DIP (依賴反轉原則): 實作TagDisplayService介面
 * - OCP (開放封閉原則): 透過介面擴展，對修改封閉
 */
public class CommandTagDisplayService implements TagDisplayService {
    
    private final KoukeNekoNametag plugin;
    
    public CommandTagDisplayService(KoukeNekoNametag plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean setActiveTag(Player player, Tag tag) {
        if (tag == null) {
            return false;
        }
        
        // SRP: 專門處理標籤設定邏輯
        List<String> commands = plugin.getConfig().getStringList("command.settag");
        return executeDisplayCommands(commands, player, tag.getDisplay());
    }
    
    @Override
    public boolean removeActiveTag(Player player) {
        // SRP: 專門處理標籤移除邏輯
        List<String> commands = plugin.getConfig().getStringList("command.remove");
        return executeDisplayCommands(commands, player, null);
    }
    
    /**
     * 執行顯示相關指令
     * 遵循DRY (Don't Repeat Yourself) 原則，避免重複程式碼
     */
    private boolean executeDisplayCommands(List<String> commands, Player player, String tagDisplay) {
        boolean logCommands = plugin.getConfig().getBoolean("debug.log_commands", true);
        
        for (String cmd : commands) {
            cmd = cmd.replace("{player}", player.getName());
            if (tagDisplay != null) {
                cmd = cmd.replace("{tag}", tagDisplay);
            }
            
            if (logCommands) {
                plugin.getLogger().info("執行顯示指令: " + cmd);
            }
            
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        
        return true;
    }
}