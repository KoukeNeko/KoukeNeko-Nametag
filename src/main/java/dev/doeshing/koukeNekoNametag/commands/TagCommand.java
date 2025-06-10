package dev.doeshing.koukeNekoNametag.commands;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import dev.doeshing.koukeNekoNametag.core.tag.Tag;
import dev.doeshing.koukeNekoNametag.core.tag.TagManager;
import dev.doeshing.koukeNekoNametag.core.tag.TagMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 標籤指令主控制器
 * 
 * 遵循SOLID原則重構：
 * - SRP (單一責任原則): 只負責指令路由和權限檢查，委派具體處理給專門的處理器
 * - OCP (開放封閉原則): 透過CommandHandler介面擴展新功能，不修改現有程式碼
 * - DIP (依賴反轉原則): 依賴CommandHandler抽象介面而非具體實作
 * - LSP (里氏替換原則): 所有CommandHandler實作都可以互相替換
 * - ISP (介面隔離原則): 使用專門的CommandHandler介面而非複雜的單一介面
 */
public class TagCommand implements CommandExecutor, TabCompleter {

    private final KoukeNekoNametag plugin;
    private final TagManager tagManager;
    private final TagMenu tagMenu;
    private final List<CommandHandler> commandHandlers; // DIP: 依賴抽象介面

    /**
     * 建立標籤指令控制器
     * 
     * DIP原則: 注入依賴而非直接建立具體實作
     * SRP原則: 初始化指令處理器責任鏈
     */
    public TagCommand(KoukeNekoNametag plugin, TagManager tagManager, TagMenu tagMenu) {
        this.plugin = plugin;
        this.tagManager = tagManager;
        this.tagMenu = tagMenu;
        this.commandHandlers = initializeCommandHandlers();
    }
    
    /**
     * 初始化指令處理器
     * OCP原則: 透過這個方法可以輕易擴展新的指令處理器而不修改現有程式碼
     */
    private List<CommandHandler> initializeCommandHandlers() {
        List<CommandHandler> handlers = new ArrayList<>();
        handlers.add(new CreateTagCommandHandler(plugin, tagManager));
        handlers.add(new RemoveTagCommandHandler(plugin, tagManager));
        handlers.add(new PlayerTagCommandHandler(plugin, tagManager));
        return handlers;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // SRP: 委派給專門的方法處理選單開啟
        if (args.length == 0) {
            return handleMenuOpen(sender);
        }
        
        // SRP: 委派給專門的方法檢查權限
        if (!hasAdminPermission(sender)) {
            plugin.getMessageManager().sendConfigMessage(sender, "error.no_permission");
            return true;
        }
        
        // OCP & 責任鏈模式: 使用處理器鏈處理不同類型的指令
        for (CommandHandler handler : commandHandlers) {
            if (handler.canHandle(args)) {
                return handler.handle(sender, args);
            }
        }
        
        // 如果沒有處理器可以處理，顯示幫助訊息
        plugin.getMessageManager().sendConfigMessage(sender, "tag.help");
        return true;
    }
    
    /**
     * 處理選單開啟
     * SRP: 專門處理選單開啟邏輯
     */
    private boolean handleMenuOpen(CommandSender sender) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendConfigMessage(sender, "error.player_only");
            return true;
        }
        
        Player player = (Player) sender;
        tagMenu.openMenu(player);
        return true;
    }
    
    /**
     * 檢查管理員權限
     * SRP: 專門處理權限檢查邏輯
     */
    private boolean hasAdminPermission(CommandSender sender) {
        String adminPermission = plugin.getConfig().getString("permission.admin", "koukeneko.admin");
        return sender.hasPermission(adminPermission);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        // SRP: 委派給專門的方法檢查權限
        if (!hasAdminPermission(sender)) {
            return completions;
        }
        
        // SRP: 委派給專門的方法處理自動完成
        return generateTabCompletions(args);
    }
    
    /**
     * 產生自動完成建議
     * SRP: 專門處理自動完成邏輯
     */
    private List<String> generateTabCompletions(String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            return getFirstArgumentCompletions(args[0]);
        }
        
        if (args.length == 2) {
            return getSecondArgumentCompletions(args);
        }
        
        if (args.length == 3) {
            return getThirdArgumentCompletions(args);
        }
        
        return completions;
    }
    
    /**
     * 獲取第一個參數的自動完成
     * SRP: 專門處理第一個參數自動完成
     */
    private List<String> getFirstArgumentCompletions(String input) {
        List<String> completions = new ArrayList<>();
        
        // 指令關鍵字
        if ("create".startsWith(input.toLowerCase())) {
            completions.add("create");
        }
        if ("remove".startsWith(input.toLowerCase())) {
            completions.add("remove");
        }
        
        // 在線玩家
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(input.toLowerCase())) {
                completions.add(player.getName());
            }
        }
        
        return completions;
    }
    
    /**
     * 獲取第二個參數的自動完成
     * SRP: 專門處理第二個參數自動完成
     */
    private List<String> getSecondArgumentCompletions(String[] args) {
        List<String> completions = new ArrayList<>();
        
        if ("create".equalsIgnoreCase(args[0])) {
            // create 後面不提供自動完成
            return completions;
        }
        
        if ("remove".equalsIgnoreCase(args[0])) {
            // remove 後面是標籤ID
            for (Tag tag : tagManager.getAllTags()) {
                if (tag.getId().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(tag.getId());
                }
            }
            return completions;
        }
        
        // 玩家名稱後面是 add/remove
        List<String> actions = List.of("add", "remove");
        for (String action : actions) {
            if (action.startsWith(args[1].toLowerCase())) {
                completions.add(action);
            }
        }
        
        return completions;
    }
    
    /**
     * 獲取第三個參數的自動完成
     * SRP: 專門處理第三個參數自動完成
     */
    private List<String> getThirdArgumentCompletions(String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (!"create".equalsIgnoreCase(args[0]) && 
            ("add".equalsIgnoreCase(args[1]) || "remove".equalsIgnoreCase(args[1]))) {
            // 如果是 /tag <player> add/remove，第三個參數是標籤ID
            for (Tag tag : tagManager.getAllTags()) {
                if (tag.getId().toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(tag.getId());
                }
            }
        }
        
        return completions;
    }
}