package wtf.wooly.combattag;

import org.bukkit.scheduler.BukkitRunnable;
import wtf.wooly.combattag.commands.*;
import wtf.wooly.combattag.listeners.*;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import me.clip.placeholderapi.PlaceholderAPI;
import wtf.wooly.combattag.resources.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class CombatTag extends JavaPlugin {
    public static final String perms = "combattag.admin";
    private static CombatTag instance;
    public static final Map<UUID, Long> playersInCombat = new ConcurrentHashMap<>();
    private static boolean papiSupport = false;
    public static BukkitTask actionBarTask;

    @Override
    public void onEnable() {
        instance = this;
        reloadAll();
        getCommand("combat-tag").setExecutor(new CT());

        getServer().getPluginManager().registerEvents(new PlayerCommandPreprocess(), this);
        getServer().getPluginManager().registerEvents(new PlayerHitPlayer(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeath(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("PlaceholderAPI found, enabling placeholders.");
            papiSupport = true;
            new PAPIExpansion().register();
        } else {
            getLogger().warning("PlaceholderAPI was not found, placeholders won't be able to be used!");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Detecting plugin shutdown, removing all active combat tags.");
        playersInCombat.clear();
    }

    public static Component deserialise(Player player, String message) {
        if (papiSupport && player != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        return MiniMessage.miniMessage().deserialize(message);
    }

    public void reloadAll() {
        saveDefaultConfig();
        reloadConfig();

        if (actionBarTask != null) {
            actionBarTask.cancel();
        }

        String actionBarMessage = getConfig().getString("in-combat-action-bar");
        long period = 5L;

        actionBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : playersInCombat.keySet()) {
                    Player player = getServer().getPlayer(uuid);
                    if (player != null && actionBarMessage != null && !actionBarMessage.isBlank()) {
                        player.sendActionBar(deserialise(player, actionBarMessage));
                    }
                }
            }
        }.runTaskTimer(this, 0L, period);
    }

    public static CombatTag getPlugin() {
        return instance;
    }
}