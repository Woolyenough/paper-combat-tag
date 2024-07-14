package wtf.wooly.combattag.listeners;

import wtf.wooly.combattag.CombatTag;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

import static wtf.wooly.combattag.CombatTag.deserialise;

public class PlayerCommandPreprocess implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        CombatTag plugin = CombatTag.getPlugin();
        if (!plugin.getConfig().getBoolean("enable-command-blocker")) {
            return;
        }
        if (!CombatTag.playersInCombat.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        String message = event.getMessage();
        String[] args = message.split(" ");

        if (plugin.getConfig().getBoolean("command-blocker.bypass-colons") && args[0].contains(":")) {
            args[0] = "/" + args[0].split(":")[1];
            message = String.join(" ", args);
        }

        List<String> blockedCommands = plugin.getConfig().getStringList("command-blocker.blocked-cmds");
        boolean matchEntireWords = plugin.getConfig().getBoolean("command-blocker.match-entire-words");

        if (matchEntireWords) {
            for (String cmd : blockedCommands) {
                String[] blockedArgs = cmd.split(" ");
                boolean match = true;

                for (int i = 0; i < blockedArgs.length; i++) {
                    if (i >= args.length || !args[i].equalsIgnoreCase(blockedArgs[i])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    cancelEvent(event);
                    return;
                }
            }
        } else {
            if (blockedCommands.contains(message.toLowerCase())) {
                cancelEvent(event);
            }
        }
    }
    private void cancelEvent(PlayerCommandPreprocessEvent event) {
        CombatTag plugin = CombatTag.getPlugin();
        event.setCancelled(true);
        String msg = plugin.getConfig().getString("command-blocker.blocked-msg");
        if (!msg.isBlank()) {
            event.getPlayer().sendMessage(deserialise(event.getPlayer(), msg));
        }
    }
}
