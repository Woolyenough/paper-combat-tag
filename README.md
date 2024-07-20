Paper/Bukit plugin that can block commands, display messages & different scoreboards when players hit each other for a specified time. Simple and lightweight.

<details><summary><b>Commands, Permissions & Placeholders</b></summary>

- `/combat-tag reload` (alias: `/ct`) - reloads the config

Command requires the permission `combattag.admin`.

**Placeholders:**
- `%ct_time_left%` - time left in combat (in seconds)
- `%ct_in_combat%` - whether the player is in combat (true/false)

_(Yeah... not much; what a simple plugin!)_
</br></details>
<details><summary><b>Default config</b></summary>

```yaml
# All messages use Adventure's MiniMessage: https://docs.advntr.dev/minimessage/format.html
# All messages support PlaceholderAPI placeholders
# Make any message an empty string "" to disable it

# Enable the plugin's features?
enabled: true

combat-duration: 20.0

# Internal placeholder: [otherPlayer]
combat-tagged-message: "<#cb0000>You are now in combat with [otherPlayer]"

in-combat-action-bar: "<#cb0000>You are in combat! <grey>(<#ffd0a8>%ct_time_left%<grey>)"

combat-expired-message: "<green>You are no longer in combat"

# Enable the command blocker?
enable-command-blocker: true
command-blocker:
  blocked-msg: "<red>You can't run this command while in combat!"

  # Enabled: if command has a colon in (e.g. /plugin:spawn), it will ignore the text before the colon, and only check it as '/spawn'
  bypass-colons: true

  # Enabled: each whole word must match (e.g., `/warp spawn` will block `/warp spawn` but not `/warp1` or `/warp spawn1`)
  match-entire-words: true

  # Enabled: if player leaves while in combat, they die and drop their items
  kill-on-quit: true

  blocked-cmds:
    - "/warp spawn"
    - "/home"
    - "/tpa"
```
</br></details>
<details><summary><b>Combat scoreboard</b></summary>

[TAB](https://github.com/NEZNAMY/TAB) offers a pretty nifty feature where you can display scoreboards determined by conditions. You can use this in conjunction with the `in_combat` placeholder offered by this plugin to create a scoreboard that is displayed when the player is in combat.

Example usage (in TAB's `config.yml`):
```yaml
scoreboard:
  scoreboards:
    # Combat scoreboard (TAB checks top to bottom)
    combat:
      display-condition: "%ct_in_combat%=true" # <- the condition
      title: Combat scoreboard
      lines:
        - ' You are in combat!'
        - ' %ct_time_left% secs left'
    # Default scoreboard
    scoreboard:
      title: Normal scoreboard
      lines:
        - ' The default scoreboard of my server!'
```
</br></details>
