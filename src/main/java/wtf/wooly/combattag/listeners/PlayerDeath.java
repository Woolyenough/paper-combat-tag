package wtf.wooly.combattag.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventPriority;
import wtf.wooly.combattag.CombatTag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        CombatTag plugin = CombatTag.getPlugin();

        Player player = event.getPlayer();

        int taskId = PlayerHitPlayer.playerTaskLog.get(player.getUniqueId());
        plugin.getServer().getScheduler().cancelTask(taskId);

        CombatTag.playersInCombat.remove(player.getUniqueId());
        player.sendActionBar(Component.empty());
    }
}
