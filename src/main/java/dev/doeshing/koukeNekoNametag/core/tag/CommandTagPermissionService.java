package dev.doeshing.koukeNekoNametag.core.tag;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * 基於指令的標籤權限服務實作
 * 
 * 遵循SOLID原則：
 * - SRP (單一責任原則): 專門負責權限相關指令執行
 * - DIP (依賴反轉原則): 實作TagPermissionService介面
 * - OCP (開放封閉原則): 透過介面擴展，對修改封閉
 */
public class CommandTagPermissionService implements TagPermissionService {
    
    private final KoukeNekoNametag plugin;
    
    public CommandTagPermissionService(KoukeNekoNametag plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean givePermission(Player player, Tag tag) {
        if (tag == null) {
            return false;
        }
        
        // SRP: 專門處理權限給予邏輯
        return executePermissionCommands("command.add_permission", player, tag);
    }
    
    @Override
    public boolean removePermission(Player player, Tag tag) {
        if (tag == null) {
            return false;
        }
        
        // SRP: 專門處理權限移除邏輯
        return executePermissionCommands("command.remove_permission", player, tag);
    }
    
    @Override
    public boolean hasPermission(Player player, Tag tag) {
        return player.hasPermission(tag.getPermission());
    }
    
    /**
     * 執行權限相關指令
     * 遵循DRY (Don't Repeat Yourself) 原則，避免重複程式碼
     */
    private boolean executePermissionCommands(String configPath, Player player, Tag tag) {
        List<String> commands = plugin.getConfig().getStringList(configPath);
        boolean logCommands = plugin.getConfig().getBoolean("debug.log_commands", true);
        
        for (String cmd : commands) {
            cmd = cmd.replace("{player}", player.getName())
                    .replace("{tag}", tag.getPermission());
            
            if (logCommands) {
                plugin.getLogger().info("執行權限指令: " + cmd);
            }
            
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        
        return true;
    }
}