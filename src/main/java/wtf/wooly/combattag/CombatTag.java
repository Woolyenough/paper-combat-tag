package wtf.wooly.combattag;

import wtf.wooly.combattag.commands.*;
import wtf.wooly.combattag.listeners.*;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.*;

public final class CombatTag extends JavaPlugin {
    public final static String perms = "combattag.admin";
    public static Map<UUID, Long> playersInCombat = new HashMap<>();
    private static boolean papiSupport = false;
    public static BukkitTask actionBarTask;

    @Override
    public void onEnable() {
        reloadAll();
        getCommand("combat-tag").setExecutor(new CT(this));

        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerHitPlayer(this), this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("PlaceholderAPI found, enabling placeholders.");
            papiSupport = true;
            new PAPIExpansion(this).register();
        }else{
            getLogger().warning("PlaceholderAPI was not found, placeholders won't be able to be used!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Component deserialise(Player player, String message){
        if(papiSupport)
            message = PlaceholderAPI.setPlaceholders(player, message);
        return MiniMessage.miniMessage().deserialize(message);
    }
    public void reloadAll(){
        saveDefaultConfig();
        reloadConfig();

        if(actionBarTask != null)
            getServer().getScheduler().cancelTask(actionBarTask.getTaskId());

        actionBarTask = getServer().getScheduler().runTaskTimer(this, () ->{
            for (UUID uuid : playersInCombat.keySet()) {
                Player player = getServer().getPlayer(uuid);
                String msg = getConfig().getString("in-combat-action-bar");
                if(!msg.isBlank())
                    player.sendActionBar(deserialise(player, getConfig().getString("in-combat-action-bar")));
            }
        }, 0L, 5L);
    }
}
