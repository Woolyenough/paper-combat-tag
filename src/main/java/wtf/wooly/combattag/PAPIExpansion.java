package wtf.wooly.combattag;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PAPIExpansion extends PlaceholderExpansion {
    private final CombatTag plugin; //

    public PAPIExpansion(CombatTag plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ct";
    }
    @Override
    public @NotNull String getAuthor() {
        return "Woolyenough";
    }
    @Override
    public @NotNull String getVersion() {
        return "1";
    }
    @Override
    public boolean persist() {
        return true;
    }
    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {

        if(params.equalsIgnoreCase("in_combat")){
            return String.valueOf(CombatTag.playersInCombat.containsKey(offlinePlayer.getUniqueId()));
        }
        if(params.equalsIgnoreCase("time_left")){
            long startedTime = CombatTag.playersInCombat.getOrDefault(offlinePlayer.getUniqueId(), 0L);
            if(startedTime == 0L) return "0";
            long nowTime = System.currentTimeMillis();
            long timeLeft = startedTime - nowTime + (this.plugin.getConfig().getInt("combat-duration") * 1000);
            int timeLeftInt = (int) timeLeft / 1000;
            if(timeLeftInt == this.plugin.getConfig().getInt("combat-duration"))
                timeLeftInt -= 1;
            return String.valueOf(timeLeftInt+1);
        }
        return "";
    }
}