package dev.doeshing.koukeNekoNametag.commands;

import org.bukkit.command.CommandSender;

/**
 * 指令處理器介面
 * 
 * 遵循SOLID原則：
 * - SRP (單一責任原則): 每個實作專門處理一種指令類型
 * - OCP (開放封閉原則): 透過介面擴展新的指令處理器
 * - DIP (依賴反轉原則): 高層模組依賴抽象介面
 * - ISP (介面隔離原則): 提供簡潔的指令處理介面
 */
public interface CommandHandler {
    
    /**
     * 處理指令
     * @param sender 指令發送者
     * @param args 指令參數
     * @return 處理是否成功
     */
    boolean handle(CommandSender sender, String[] args);
    
    /**
     * 獲取指令名稱
     * @return 指令名稱
     */
    String getCommandName();
    
    /**
     * 檢查是否能處理此指令
     * @param args 指令參數
     * @return 是否能處理
     */
    boolean canHandle(String[] args);
}