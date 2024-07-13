package wtf.wooly.combattag.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;
import wtf.wooly.combattag.CombatTag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static wtf.wooly.combattag.CombatTag.deserialise;

public class PlayerHitPlayer implements Listener {
    private CombatTag plugin;

    public static Map<UUID, Integer> playerTaskLog = new HashMap<>();

    public PlayerHitPlayer(CombatTag plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if(this.plugin.getConfig().getBoolean("enabled")) {
            if ((e.getEntity().getType() == EntityType.PLAYER) && (e.getDamager().getType() == EntityType.PLAYER)) {

                Player attacker = (Player) e.getDamager();
                Player victim = (Player) e.getEntity();

                for(Player player: new Player[]{attacker, victim}) {
                    if(CombatTag.playersInCombat.containsKey(player.getUniqueId()))
                        renewTimer(player);

                    if(!CombatTag.playersInCombat.containsKey(player.getUniqueId())) {
                        if(player == attacker)
                            inCombat(player, victim.getName());
                        else
                            inCombat(player, attacker.getName());

                    }
                }
            }
        }
    }
    public void inCombat(Player player, String with) {
        CombatTag.playersInCombat.put(player.getUniqueId(), System.currentTimeMillis());
        String msg = this.plugin.getConfig().getString("combat-tagged-message");
        if(!msg.isBlank())
            player.sendMessage(deserialise(player, msg.replace("[otherPlayer]", with)));

        BukkitTask task = this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            outCombat(player);
        }, this.plugin.getConfig().getLong("combat-duration") * 20L);
        playerTaskLog.put(player.getUniqueId(), task.getTaskId());
    }
    public void outCombat(Player player) {
        CombatTag.playersInCombat.remove(player.getUniqueId());
        String msg = this.plugin.getConfig().getString("combat-expired-message");
        if(!msg.isBlank())
            player.sendMessage(deserialise(player, this.plugin.getConfig().getString("combat-expired-message")));
        player.sendActionBar(Component.empty());
    }
    public void renewTimer(Player player){
        CombatTag.playersInCombat.replace(player.getUniqueId(), System.currentTimeMillis());

        this.plugin.getServer().getScheduler().cancelTask(playerTaskLog.get(player.getUniqueId()));
        BukkitTask task = this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            outCombat(player);
        }, this.plugin.getConfig().getLong("combat-duration") * 20L);
        playerTaskLog.put(player.getUniqueId(), task.getTaskId());
    }
}
