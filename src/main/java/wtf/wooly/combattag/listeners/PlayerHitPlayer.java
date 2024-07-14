package wtf.wooly.combattag.listeners;

import wtf.wooly.combattag.CombatTag;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static wtf.wooly.combattag.CombatTag.deserialise;

public class PlayerHitPlayer implements Listener {
    public static final Map<UUID, Integer> playerTaskLog = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        CombatTag plugin = CombatTag.getPlugin();
        if (!plugin.getConfig().getBoolean("enabled")) return;

        if (e.getEntity().getType() == EntityType.PLAYER && e.getDamager().getType() == EntityType.PLAYER) {
            Player attacker = (Player) e.getDamager();
            Player victim = (Player) e.getEntity();

            handleCombat(attacker, victim);
            handleCombat(victim, attacker);
        }
    }

    private void handleCombat(Player player, Player opponent) {
        UUID playerId = player.getUniqueId();

        if (CombatTag.playersInCombat.containsKey(playerId)) {
            renewTimer(player);
        } else {
            inCombat(player, opponent.getName());
        }
    }

    public void inCombat(Player player, String with) {
        CombatTag plugin = CombatTag.getPlugin();
        UUID playerId = player.getUniqueId();
        CombatTag.playersInCombat.put(playerId, System.currentTimeMillis());
        String msg = plugin.getConfig().getString("combat-tagged-message");
        if (msg != null && !msg.isBlank()) {
            player.sendMessage(deserialise(player, msg.replace("[otherPlayer]", with)));
        }

        long combatDuration = plugin.getConfig().getLong("combat-duration") * 20L;
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> outCombat(player), combatDuration);
        playerTaskLog.put(playerId, task.getTaskId());
    }

    public void outCombat(Player player) {
        CombatTag plugin = CombatTag.getPlugin();

        int taskId = playerTaskLog.get(player.getUniqueId());
        plugin.getServer().getScheduler().cancelTask(taskId);

        UUID playerId = player.getUniqueId();
        CombatTag.playersInCombat.remove(playerId);

        String msg = plugin.getConfig().getString("combat-expired-message");
        if (msg != null && !msg.isBlank()) {
            player.sendMessage(deserialise(player, msg));
        }

        player.sendActionBar(Component.empty());
    }

    public void renewTimer(Player player) {
        CombatTag plugin = CombatTag.getPlugin();
        UUID playerId = player.getUniqueId();
        CombatTag.playersInCombat.put(playerId, System.currentTimeMillis());

        int taskId = playerTaskLog.get(playerId);
        plugin.getServer().getScheduler().cancelTask(taskId);

        long combatDuration = plugin.getConfig().getLong("combat-duration") * 20L;
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> outCombat(player), combatDuration);
        playerTaskLog.put(playerId, task.getTaskId());
    }
}