package wtf.wooly.combattag.listeners;

import wtf.wooly.combattag.CombatTag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        CombatTag plugin = CombatTag.getPlugin();

        if(!plugin.getConfig().getBoolean("kill-on-quit")) return;

        Player player = event.getPlayer();
        if (CombatTag.playersInCombat.containsKey(player.getUniqueId())) {
            player.setHealth(0.0);
            CombatTag.playersInCombat.remove(player.getUniqueId());
        }
    }
}
