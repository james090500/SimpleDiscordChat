
# 🎙️ SimpleDiscordChat
A modern solution for linking multiple servers to Discord without the bloat!

## 📜 Permissions
| Permission | Purpose |  
|--|--|  
| `discord.link` | Permission for `/discord link` |

## 💻 Commands
| Command | Response |  
|--|--|  
| `/discord` | Show the discord message |

## 📑 Config
```yaml
## Bot Settings  
# The token of your bot  
botToken: ""  
# The channel of the bot  
chatChannel: ""  
  
## Chat Formats  
# Minecraft section (This is what displays in minecraft)  
# %username% = Discord username  
# %message% = Discord message  
#  
# Discord section (This is what displays in discord)  
# %player_name% = Players Username  
# %player_nick% = Players Display Name (usually their nickname)  
# %player_uuid% = Players UUID  
# %message% = Chat message  
format:  
  minecraft: "&7[&aD&7]&r <%username%> %message%"  
  discord: "%player_nick%: %message%"  
  
## First Join, Join and Leave messages  
# %username = Minecraft username  
firstJoin:  
  enabled: true  
  color: 16711915  
  message: "\uD83C\uDF89 %username% has joined the server for the first time! \uD83C\uDF89"  
join:  
  enabled: true  
  color: 65280  
  message: "%username% has joined the server"  
leave:  
  enabled: true  
  color: 16711680  
  message: "%username% has left the server"  
  
## Advancements  
advancement:  
  enabled: true  
  color: 16777045  
  message: "%username% has made the advancement %advancement%!"  
  
## Linking and Syncing  
# If linking is true, the bot will respond to DMs for codes obtained with /discord link  
# If syncgroup is true, the bot will sync users groups to relative ones in discord  
# If syncnames is true, the bot will sync users nickname/username to relative ones in discord  
# The bot will still try sync if linking is false, this allows you to use the bot on multiple small servers  
# You will also need to configure the advanced options below these three  
linking: true  
syncGroups: true  
syncUsernames: true
```  