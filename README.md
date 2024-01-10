
# Adup - Latest: 1.5

To keep the projects alive:

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/U7U4T5BU3)

A Dupe Detection plugin for your minecraft server [1.13.x - 1.20.x]
## Why this plugin?

There are not many dupe detection plugins out. There are some popular Anti-Dupe plugins, but all these vanilla dupes are fixed in spigot, bukkit and paper. If a plugin causes a dupe like some does, ADUP is here for you and detects if a player got unusually many specific items dropped, picked up or moved into e.g. a chest in a specific time.

This plugin also contains many features and a user friendly interface.
## Download

[Download - 1.5 - Latest](https://ko-fi.com/post/1-5--Adup-Rework-L3L0T7GJA)
## Report Issues

You can report issues in the github repository.
## Commands

- /adup reload : Reloads the config and tracked items
- /adup bans : Opens a menu to manage and view banned players, through the auto ban system
- /adup suspects : Opens a menu to manage and spectate suspected players
- /adup unban <player> : Unbans the specified player
- /adup logs <player> : Opens a menu to manage and view the logs of the specified player (logs are items that the player dropped, picked up or moved)
- /adup tracked remove <name> : Removes a tracked item with the specified name
- /adup tracked add : Opens a menu to add a tracked item
- /adup mce : Opens a menu to manage the whitelist and toggle maintenance
- /adup spectate <player> : Hides you from all players and teleports you to the specified player  
## Permissions

- admin: Permission for every permission below
- track_bypass: Permission to don't track the player (doesn't create logs and doesn't ban/warn)
- banned_bypass: Permission to bypass the auto ban
- maintenance_bypass: Permission to bypass the maintenance whitelist
- receive_alerts: Permission to receive alerts
- maintenance: Permission to use /adup mce
- spectate: Permission to use /adup spectate <player>
- unban: Permission to use /adup unban <player>
- bans: Permission to use /adup bans
- logs: Permission to use /adup logs <player>
- suspects: Permission to use /adup suspects
- reload: Permission to use /adup reload
- tracked: Permission to use /adup tracked
## Default Config

```yml
# Source: https://www.spigotmc.org/resources/1-13-x-1-20-x-adup-dupe-detection.110062/
# Discord: https://discord.gg/46R3fmqGJ6
# Docs: https://github.com/deneonet/adup
# Developer: Debion / Deneo

permissions:
  # Permission for every permission below
  admin: "adup.admin"
  # Permission to don't track the player (doesn't create logs and doesn't ban/warn)
  track_bypass: "adup.track_bypass"
  # Permission to bypass the auto ban
  banned_bypass: "adup.banned_bypass"
  # Permission to bypass the maintenance whitelist
  maintenance_bypass: "adup.maintenance_bypass"
  # Permission to receive alerts
  receive_alerts: "adup.receive.alerts"
  maintenance: "adup.maintenance" # /adup mce
  spectate: "adup.spectate" # /adup spectate <player>
  unban: "adup.unban" # /adup unban <player>
  bans: "adup.bans" # /adup bans
  logs: "adup.logs" # /adup logs <player>
  suspects: "adup.suspects" # /adup suspects
  reload: "adup.reload" # /adup reload
  tracked: "adup.tracked" # /adup tracked

date_time:
  lang_tag: "en-US"
  pattern: "MM-dd-yyyy HH:mm:ss"

auto_ban:
  enabled: true

  # The max warnings a player has to reach before getting banned
  max_warnings: 3
  ban_duration: "1h"

  # Placeholders:
  # {duration} = the ban duration specified above
  ban_reason: "§5You're banned §7for §5{duration}§7, because you're under §5suspicion for duping§7. Please let the mods some time to check that."

discord:
  send_alerts: false
  webhook_url: # Your Webhook Url here

  # The name for the bot
  bot_username: "ADUP \ Dupe Detection By Debion"
  # The avatar url for the bot
  bot_avatar: "https://i.imgur.com/xRAc4TL.png"

maintenance:
  enabled: false
  kick_reason: "§7Server is currently under §5maintenance§7. Try again later."

messages:
  # The message to send...
  # ... when a player hasn't specified a target in a command
  #
  # Placeholders:
  # {command} = the command that got executed without a target
  missing_target: "§5§lADUP §8§l» §7Missing target use {command}!"
  # ... if a player without permissions tries to execute a command
  #
  # Placeholders:
  # {command} = the command that got executed without a target
  insufficient_permissions: "§5§lADUP §8§l» §7Sorry, I can't let you use {command}!"
  # ... if a tracked item got successfully removed
  #
  # Placeholders:
  # {name} = the name of the tracked item, that got removed
  tracked_item_removed: "§5§lADUP §8§l» §7Removed tracked item: §5{name}!"
  # ... if a tracked item got successfully added
  #
  # Placeholders:
  # {name} = the name of the tracked item, that got added
  tracked_item_added: "§5§lADUP §8§l» §7Added tracked item: §5{name}!"
  # ... if a player got successfully unbanned
  #
  # Placeholders:
  # {target} = the name of the unbanned player
  unbanned: "§5§lADUP §8§l» §5{target} §7got unbanned!"
  # ... if a player spectates its target
  #
  # Placeholders:
  # {target} = the name of the spectated player
  spectating_message: "§5§lADUP §8§l» §7You are now spectating §5{target}!"
  # ... if a player no longer spectates its target
  #
  # Placeholders:
  # {target} = the name of the no longer spectated player
  un_spectating_message: "§5§lADUP §8§l» §7You are not spectating §5{target} §7anymore!"
  # ... if the specified target is not online
  player_not_online: "§5§lADUP §8§l» §7This player is not online!"
  # ... if the player doesn't hold an item in the main hand, when executing '/adup tracked add ...'
  no_holding_item: "§5§lADUP §8§l» §7Hold the item to track in your main hand!"
  # ... if the player/console tries to execute a non-existing command or the command is invalid
  invalid_command_console: "§5§lADUP §8§l» §7Commands\n§5/adup reload\n/adup mce\n/adup tracked remove <name>"
  invalid_command_player: "§5§lADUP §8§l» §7Commands\n§5/adup logs <player>\n/adup unban <player>\n/adup reload\n/adup mce\n/adup tracked remove <name>\n/adup tracked add\n/adup suspects\n/adup bans\n/adup spectate <player>"
  # ... if the player tries to remove a tracked item using '/adup tracked remove ...', but the tracked item doesn't exist
  tracked_item_does_not_exists: "§5§lADUP §8§l» §7A tracked item with that name doesn't exists."
  # ... if the player tries to add a tracked item using '/adup tracked add ...', but the tracked item already exists
  tracked_item_already_exists: "§5§lADUP §8§l» §7A tracked item with that name already exists."

# The interval between the cached data gets saved into the database (in minutes)
auto_save_data_interval: 5

mysql:
  # Determines if the MySQL database is enabled, if not SQLite is used
  enabled: false
  # Credentials for the MySQL database
  host: "localhost"
  port: 3306
  database: "db"
  user: "username"
  password: "passy"

tracking:
  send_chat_alerts: true

  # Placeholders:
  # {tracked_name} = the custom name that the tracked item has
  # {item_type} = the type of the item (DIRT, DIAMOND_BLOCK, etc.)
  # {item_amount} = the amount of item
  item_format: "§5{tracked_name}§7 of type §5{item_type} x{item_amount}"
  item_format_no_name: "§5{item_type} x{item_amount}"

  # Placeholders:
  # {player} = the name of the player
  # {type} = (dropping | picking up (self-dropped) | picking up (from unknown) | picking up | moving)
  # -> all specified beneath: DROPPED, etc.
  # {item} = the item in format of 'item_format' or 'item_format_un_tracked' specified above
  # {world} = the world that the player is currently in
  # {warnings} = the current warnings that the player has
  warned_alert: "§5§lADUP §8§l» §5{player}§7 got warned for {type} §5{item}§7. In §5{world}§7. Warnings: §5{warnings}\n§7View the logs for more info."
  banned_alert: "§5§lADUP §8§l» §5{player}§7 got banned for {type} §5{item}§7. In §5{world}§7. View the logs for more info."

  # Enables/Disables the tracking of dropped, moved or picked up
  drop: true
  move: true
  pick_up: true

  # Placeholder translations for '{type}' in 'warned_alert' and 'banned_alert'
  DROPPED: "dropping"
  PICKED_UP_SELF_DROPPED: "picking up (self-dropped)"
  PICKED_UP_PLAYER: "picking up"
  PICKED_UP_UNKNOWN: "picking up (from unknown)"
  MOVED: "moving"

  time_span: "2h"

  items:
    -
```
