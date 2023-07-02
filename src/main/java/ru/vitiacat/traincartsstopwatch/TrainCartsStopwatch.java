package ru.vitiacat.traincartsstopwatch;

import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class TrainCartsStopwatch extends JavaPlugin {

    private static StopwatchSign stopwatchSign = new StopwatchSign();
    public static TrainCartsStopwatch Instance;

    @Override
    public void onEnable() {
        Instance = this;
        if(TCConfig.SignLinkEnabled) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, StopwatchSign::updateAll, 0, 20);
            SignAction.register(stopwatchSign);
            getLogger().info("Registered StopwatchSign");
        } else {
            getLogger().info("SignLink disabled :(");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        SignAction.unregister(stopwatchSign);
        getLogger().info("Unregistered StopwatchSign");
    }
}
