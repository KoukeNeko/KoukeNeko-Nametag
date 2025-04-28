package dev.doeshing.koukeNekoNametag.core;

import dev.doeshing.koukeNekoNametag.KoukeNekoNametag;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

public class CommandSystem {

    private final KoukeNekoNametag plugin;

    public CommandSystem(KoukeNekoNametag plugin) {
        this.plugin = plugin;
    }

    /**
     * 通用指令註冊方法
     *
     * @param commandName       指令名稱 (不含 "/"，例如 "setlobby")
     * @param executor          指令執行器，實作 CommandExecutor
     * @param commandPermission 權限字串 (可為 null 或 "")
     * @param commandDescription 指令說明 (可為 null)
     * @param commandUsage      使用方式 (可為 null)
     * @param commandAliases    指令別名 (可為空陣列)
     */
    public void registerCommand(
            String commandName,
            CommandExecutor executor,
            String commandPermission,
            String commandDescription,
            String commandUsage,
            String... commandAliases
    ) {
        try {
            // 1. 拿到 Bukkit 內部的 CommandMap
            Field commandMapField = plugin.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(plugin.getServer());

            // 2. 透過反射建立 PluginCommand 實例 (建構子是 protected，需要反射)
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand pluginCommand = constructor.newInstance(commandName, plugin);

            // 3. 指令屬性設定
            pluginCommand.setExecutor(executor); // 誰來執行指令邏輯

            // 4. 如果執行器也實現了 TabCompleter 接口，則設定為 Tab 補全器
            if (executor instanceof TabCompleter) {
                pluginCommand.setTabCompleter((TabCompleter) executor);
            }

            if (commandPermission != null && !commandPermission.isEmpty()) {
                pluginCommand.setPermission(commandPermission);
            }
            if (commandDescription != null) {
                pluginCommand.setDescription(commandDescription);
            }
            if (commandUsage != null) {
                pluginCommand.setUsage(commandUsage);
            }
            if (commandAliases != null && commandAliases.length > 0) {
                pluginCommand.setAliases(Arrays.asList(commandAliases));
            }

            // 5. 將指令註冊到 CommandMap
            commandMap.register(plugin.getName(), pluginCommand);

            plugin.getLogger().info("已註冊指令: " + commandName);

        } catch (Exception e) {
            plugin.getLogger().severe("註冊指令 " + commandName + " 時發生錯誤:");
            e.printStackTrace();
        }
    }
}
