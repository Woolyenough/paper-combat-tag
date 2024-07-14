package wtf.wooly.combattag.commands;

import wtf.wooly.combattag.CombatTag;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CT implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission(CombatTag.perms)){
            if(args.length >= 1 && args[0].equals("reload")) {
                CombatTag plugin = CombatTag.getPlugin();
                plugin.getServer().getScheduler().runTask(plugin, plugin::reloadAll);
                sender.sendMessage("Reloaded config.");
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission(CombatTag.perms) && args.length == 1)
            return List.of("reload");
        return List.of();
    }
}
