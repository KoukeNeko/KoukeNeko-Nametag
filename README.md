# KoukeNeko Nametag Plugin
<img width="1296" alt="image" src="https://github.com/user-attachments/assets/c5586f29-8247-4b17-85c1-e307e4ebb2fe" />
<img width="1296" alt="image" src="https://github.com/user-attachments/assets/28ad0478-b32f-4e7e-9e31-309d237e00e2" />



## 🌟 Introduction

KoukeNeko Nametag is a powerful and user-friendly Minecraft tag/prefix management plugin that allows players to select and display custom tags in front of their names with an intuitive GUI menu.

## 🔧 Features

- ✅ Easy-to-use GUI for selecting tags
- ✅ Permission-based tag access
- ✅ Custom tag creation with color code support
- ✅ Admin commands for tag management
- ✅ Integration with LuckPerms and TAB plugin
- ✅ Persistent tag storage and configuration
- ✅ Simple command to reload configuration
- 🆕 **Highly customizable config.yml with new sections**
- 🆕 **Flexible menu settings (title, size, materials, positions)**
- 🆕 **Configurable command details (names, aliases, descriptions)**
- 🆕 **File management options and debug settings**
- 🆕 **Multi-language support with customizable language files**

## 📂 Installation

1. Place the `KoukeNeko-Nametag.jar` file into your server's `plugins` folder
2. Restart your server or use the `/reload confirm` command
3. Configure the plugin settings in `config.yml` and `tags.yml` as needed

> **Note:** This plugin can work with any tag display method. The default configuration uses TAB Plugin and LuckPerms commands, but you can modify these in the config.yml to work with other plugins.

## 🚀 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/tag` | Open the tag selection menu | None |
| `/tag create <tag-id> <display-format>` | Create a new tag | `koukeneko.admin` |
| `/tag remove <tag-id>` | Delete a tag | `koukeneko.admin` |
| `/tag <player> add <tag-id>` | Give a player permission to use a tag | `koukeneko.admin` |
| `/tag <player> remove <tag-id>` | Remove tag permission from a player | `koukeneko.admin` |
| `/koukeneko reload` (`/kn reload`) | Reload plugin configuration | `koukeneko.admin` |

## 🔑 Permissions

| Permission | Description |
|------------|-------------|
| `koukeneko.admin` | Access to all administrative commands |
| `koukeneko.tags.<tag-id>` | Permission to use a specific tag |

## ⚙️ Configuration

### config.yml

The config.yml file has been significantly expanded with new customization options:

```yaml
# Basic Settings
prefix: "&7[&6&l標籤&7&l系統&7]&f" # Message prefix
language: "zh_TW" # Supported languages: zh_TW, en_US

# Permission Settings
permission:
  admin: "koukeneko.admin" # Admin permission
  tag_prefix: "koukeneko.tags." # Tag permission prefix

# Menu Customization
menu:
  title: "&8Tag Selection" # Menu title
  rows: "auto" # Menu rows (auto calculate or 1-6)
  remove_item:
    material: "BARRIER" # Remove button material
    position: "bottom_right" # Button position
  tag_item:
    material: "NAME_TAG" # Tag item material

# Command Configuration
commands:
  main:
    name: "koukeneko"
    aliases: ["kn"]
    description: "KoukeNeko plugin main command"
    usage: "/koukeneko reload"
    permission: "koukeneko.admin"
  tag:
    name: "tag"
    aliases: ["tags"]
    description: "Tag system command"
    usage: "/tag [arguments]"

# Commands to execute when tags are selected/modified
command: 
  settag: # When setting a tag
    - "tab player {player} tagprefix {tag}"
    - "tab player {player} tabprefix {tag}"
  remove: # When removing a tag
    - "tab player {player} tagprefix"
    - "tab player {player} tabprefix"
  add_permission: # When adding permission
    - "lp user {player} permission set {tag}" 
  remove_permission: # When removing permission
    - "lp user {player} permission unset {tag}"
  remove_permission_all: # When removing permission from all players
    - "lp group default permission unset {tag}"
    - "lp user * permission unset {tag}"

# File Settings
files:
  tags_file: "tags.yml" # Tag file name
  language_file_prefix: "lang_" # Language file prefix
  language_file_suffix: ".yml" # Language file suffix

# Debug Settings
debug:
  enabled: false # Debug mode toggle
  log_commands: true # Log executed commands
  log_permissions: false # Log permission checks
```

### tags.yml

```yaml
# Tag definitions
tags:
  # Format:
  # tag-id:
  #   display: Tag display format (supports & color codes)
  
  # Member tag
  member:
    display: "&7[&a玩家&7]&f"
  
  # VIP tag
  vip:
    display: "&7[&6VIP&7]&f"
  
  # Admin tag
  admin:
    display: "&7[&cop&7]&f"
  
  # Beta tester tag
  beta:
    display: "&7[&bBeta&7]&f"

  # Developer tag
  dev:
    display: "&7[&5Dev&7]&f"
```

## 📱 Usage

### For Players
1. Use the `/tag` command to open the tag selection GUI
2. Click on any available tag to apply it to your name
3. Click the barrier icon to remove your current tag

### For Admins
1. Create tags with `/tag create <id> <display>` (e.g., `/tag create mvp &7[&bMVP&7]&f`)
2. Grant players permission with `/tag <player> add <id>`
3. Remove tags with `/tag remove <id>` if needed

## ⚠️ Important Notes

- Color codes use the `&` symbol (e.g., `&7[&aVIP&7]` for gray brackets with a green "VIP")
- The default configuration uses commands compatible with TAB plugin and LuckPerms, but you can modify these in config.yml
- The tags.yml file contains default tags, which you can modify or add to as needed
- When removing a tag, all online players will receive a notification if they had that tag
- 🆕 **After updating, the plugin will automatically update your config.yml with new sections while preserving existing settings**
- 🆕 **You can now customize menu appearance, command settings, and file locations**
- 🆕 **Debug mode is available for troubleshooting - enable `debug.enabled` for detailed logs**

## 🤝 Contribution

Feel free to submit Issues or Pull Requests if you encounter any problems or have suggestions for additional features.

## 📜 License

This plugin is released under the MIT License.

---

## 🌟 簡介

KoukeNeko Nametag 是一個功能強大且使用者友好的 Minecraft 稱號/前綴管理插件，允許玩家通過直觀的 GUI 選單選擇並在名字前顯示自定義標籤。

## 🔧 功能特色

- ✅ 簡單易用的標籤選擇 GUI 界面
- ✅ 基於權限的標籤使用控制
- ✅ 支援顏色代碼的自定義標籤建立
- ✅ 完整的管理員標籤管理指令
- ✅ 與 LuckPerms 和 TAB 插件的無縫整合
- ✅ 永久性標籤存儲和設定
- ✅ 簡單的設定重載指令
- 🆕 **高度可自訂的 config.yml 配置檔案**
- 🆕 **靈活的選單設定（標題、大小、材質、位置）**
- 🆕 **可配置的指令詳細設定（名稱、別名、描述）**
- 🆕 **檔案管理選項和除錯設定**
- 🆕 **多語言支援與可自訂語言檔案**

## 📂 安裝方式

1. 將 `KoukeNeko-Nametag.jar` 文件放入伺服器的 `plugins` 文件夾
2. 重啟伺服器或使用 `/reload confirm` 指令
3. 根據需要在 `config.yml` 和 `tags.yml` 中設定插件設定

> **注意：** 本插件可以與任何稱號顯示方式搭配使用。預設設定使用了TAB插件和LuckPerms的指令，但您可以在config.yml中修改這些設定以配合其他插件。

## 🚀 指令

| 指令 | 描述 | 權限 |
|------|------|------|
| `/tag` | 打開標籤選擇選單 | 無 |
| `/tag create <標籤ID> <顯示格式>` | 建立新標籤 | `koukeneko.admin` |
| `/tag remove <標籤ID>` | 刪除標籤 | `koukeneko.admin` |
| `/tag <玩家> add <標籤ID>` | 給予玩家使用標籤的權限 | `koukeneko.admin` |
| `/tag <玩家> remove <標籤ID>` | 移除玩家的標籤權限 | `koukeneko.admin` |
| `/koukeneko reload` (`/kn reload`) | 重新載入插件設定 | `koukeneko.admin` |

## 🔑 權限

| 權限 | 描述 |
|------|------|
| `koukeneko.admin` | 擁有所有管理指令的權限 |
| `koukeneko.tags.<標籤ID>` | 使用特定標籤的權限 |

## ⚙️ 設定

### config.yml

config.yml 檔案已大幅擴展，新增了許多自訂選項：

```yaml
# 基本設定
prefix: "&7[&6&l標籤&7&l系統&7]&f" # 訊息前綴
language: "zh_TW" # 支援的語言: zh_TW, en_US

# 權限設定
permission:
  admin: "koukeneko.admin" # 管理員權限
  tag_prefix: "koukeneko.tags." # 標籤權限前綴

# 選單自訂設定
menu:
  title: "&8標籤選擇" # 選單標題
  rows: "auto" # 選單行數 (auto 自動計算或 1-6)
  remove_item:
    material: "BARRIER" # 移除按鈕材質
    position: "bottom_right" # 按鈕位置
  tag_item:
    material: "NAME_TAG" # 標籤物品材質

# 指令設定
commands:
  main:
    name: "koukeneko"
    aliases: ["kn"]
    description: "KoukeNeko 插件主指令"
    usage: "/koukeneko reload"
    permission: "koukeneko.admin"
  tag:
    name: "tag"
    aliases: ["標籤", "tags"]
    description: "標籤系統指令"
    usage: "/tag [參數]"

# 當選擇標籤後要執行的系統指令
command: 
  settag: # 設定標籤
    - "tab player {player} tagprefix {tag}"
    - "tab player {player} tabprefix {tag}"
  remove: # 移除標籤
    - "tab player {player} tagprefix"
    - "tab player {player} tabprefix"
  add_permission: # 增加權限
    - "lp user {player} permission set {tag}" 
  remove_permission: # 移除權限
    - "lp user {player} permission unset {tag}" 
  remove_permission_all: # 移除所有玩家的標籤權限
    - "lp group default permission unset {tag}" 
    - "lp user * permission unset {tag}"

# 檔案設定
files:
  tags_file: "tags.yml" # 標籤檔案名稱
  language_file_prefix: "lang_" # 語言檔案前綴
  language_file_suffix: ".yml" # 語言檔案後綴

# 除錯設定
debug:
  enabled: false # 除錯模式開關
  log_commands: true # 記錄執行的指令
  log_permissions: false # 記錄權限檢查
```

### tags.yml

```yaml
# 標籤定義
tags:
  # 格式：
  # 標籤ID:
  #   display: 顯示格式 (支援 & 顏色代碼)
  
  # 普通會員標籤
  member:
    display: "&7[&a玩家&7]&f"
  
  # VIP標籤
  vip:
    display: "&7[&6VIP&7]&f"
  
  # 管理員標籤
  admin:
    display: "&7[&cop&7]&f"
  
  # BETA測試者標籤
  beta:
    display: "&7[&bBeta&7]&f"

  # 伺服器開發者標籤
  dev:
    display: "&7[&5Dev&7]&f"
```

## 📱 使用方法

### 玩家使用
1. 使用 `/tag` 指令打開標籤選擇界面
2. 點選任何可用標籤將其應用到您的名字
3. 點選障礙物圖標（右下角）移除目前標籤

### 管理員使用
1. 使用 `/tag create <ID> <顯示>` 建立標籤（例如：`/tag create mvp &7[&bMVP&7]&f`）
2. 使用 `/tag <玩家> add <ID>` 授予玩家權限
3. 如需移除標籤，使用 `/tag remove <ID>`

## ⚠️ 注意事項

- 顏色代碼使用 `&` 符號（例如：`&7[&aVIP&7]` 表示灰色括號內的綠色「VIP」文字）
- 預設設定使用了與 TAB 插件和 LuckPerms 相容的指令，但您可以在 config.yml 中修改這些設定
- tags.yml 文件包含預設標籤，您可以根據需要修改或新增新標籤
- 刪除標籤時，所有擁有該標籤權限的在線玩家都會收到通知
- 🆕 **更新後，插件會自動更新您的 config.yml 檔案，保留現有設定並新增新的區段**
- 🆕 **現在您可以自訂選單外觀、指令設定和檔案位置**
- 🆕 **提供除錯模式用於故障排除 - 啟用 `debug.enabled` 可獲得詳細日誌**

## 🤝 貢獻

若發現任何問題或希望提供功能建議，歡迎提交 Issue 或 Pull Request。

## 📜 授權協議

本插件基於 MIT 許可協議發布。

---

## 📝 Latest Updates

### 🆕 Configuration File Overhaul
This version brings a major update to the configuration system:
- **Enhanced customization**: Detailed menu, permission, and command settings
- **Multi-language support**: Improved language file management
- **Debug options**: Better troubleshooting capabilities
- **Flexible file management**: Customizable file names and paths

For detailed information about the new configuration options, see:
- [English Version Notes](VERSION_NOTES_EN.md)
- [中文版本說明](VERSION_NOTES_ZH.md)
- [Bilingual Version](VERSION_NOTES.md)

---

🚀 Enjoy your game! 祝你遊戲愉快！
