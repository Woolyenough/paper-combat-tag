package wtf.wooly.combattag.listeners;

import wtf.wooly.combattag.CombatTag;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

import static wtf.wooly.combattag.CombatTag.deserialise;

public class CommandListener implements Listener {
    private final CombatTag plugin;
    public CommandListener(CombatTag plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (this.plugin.getConfig().getBoolean("enable-command-blocker")){
            if (CombatTag.playersInCombat.containsKey(event.getPlayer().getUniqueId())) {
                String command = event.getMessage().split(" ")[0];
                if (this.plugin.getConfig().getBoolean("command-blocker.bypass-colons")) {
                    if (command.contains(":")) {
                        command = "/" + command.split(":")[1];
                    }
                }
                List<String> blockedCommands = this.plugin.getConfig().getStringList("command-blocker.blocked-cmds");
                if (blockedCommands.contains(command)) {
                    event.setCancelled(true);
                    String msg = this.plugin.getConfig().getString("command-blocker.blocked-msg");
                    if (!msg.isBlank())
                        event.getPlayer().sendMessage(deserialise(event.getPlayer(), msg));
                }
            }
        }
    }
}
