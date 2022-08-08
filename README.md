
# 🎙️ SimpleDiscordChat
A modern solution for linking multiple servers to Discord without the bloat!

## 📜 Permissions
| Permission | Purpose |  
|--|--|  
| `discord.link` | Permission for `/discord link` |
| `discord.unlink` | Permission for `/discord unlink` |

## 💻 Commands
| Command | Response |  
|--|--|  
| `/discord` | Show the discord message |
| `/discord link` | Start linking discord and minecraft |
| `/discord unlink` | Unlink a minecraft account |

## 📑 Config
```yaml
## Bot Settings
# The token of your bot
botToken: ""
# The channel of the bot
chatChannel: ""

## Commands
# The /discord command message
command: "&aThis is the discord server https://discord.gg/capecraft"

## Chat Formats
# Minecraft section (This is what displays in minecraft)
# %username = Discord username
# %message% = Discord message
#
# Discord section (This is what displays in discord)
# %message% = Chat message
# Use PAPI placeholders here
format:
  minecraft: "&7[&bD&7]&r &%role_color%&l%role_name% &9»&r %username%&7: &r%message%"
  discord: "%player_displayname%: %message%"

## First Join, Join and Leave messages
# Use placeholders (PAPI) for the player username
firstJoin:
  enabled: true
  color: 16711915
  message: "\uD83C\uDF89 %player_name% has joined the server for the first time! \uD83C\uDF89"
join:
  enabled: true
  color: 65280
  message: "%player_name% has joined the server"
leave:
  enabled: true
  color: 16711680
  message: "%player_name% has left the server"

## Advancements
advancement:
  enabled: true
  color: 16777045
  message: "%payer_displayname% has made the advancement %advancement%!"

## Linking and Syncing
# If linking is true, the bot will respond to DMs for codes obtained with /discord link
# If syncgroup is true, the bot will sync users groups to relative ones in discord
# If syncnames is true, the bot will sync users nickname/username to relative ones in discord
# The bot will still try sync if linking is false, this allows you to use the bot on multiple small servers
# You will also need to configure the advanced options below these three
linking: true
syncGroups: true
syncUsernames: true

syncing:
  #These are groups to sync (minecraft group name: discord group id)
  groups: {
    "default": "930579786542227488",
    "player": "930579891760541807",
    "regular": "930580013995143252",
    "member": "930580100397797396",
    "elder": "930580215707611206",
    "veteran": "485904549195022351",
    "champion": "666376095725256745",
    "legend": "598617504981450759",
    "nitro": "585542844966633472",
    "respected": "401199394763833344",
    "premium": "401199308394725376",
    "vip": "399902301319593984"
  }

  # The username placeholder to sync.
  username: "%payer_displayname%"
```  