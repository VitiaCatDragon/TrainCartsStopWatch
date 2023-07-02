package ru.vitiacat.traincartsstopwatch;

import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.tc.ArrivalSigns;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.bergerkiller.bukkit.tc.utils.TimeDurationFormat;

import java.util.HashMap;

public class StopwatchSign extends SignAction {
    private static final TimeDurationFormat timeFormat = new TimeDurationFormat("HH:mm:ss");
    private static final HashMap<String, TimeSign> timerSigns = new HashMap<>();

    public static TimeSign getStopwatch(String name) {
        return timerSigns.computeIfAbsent(name, TimeSign::new);
    }

    @Override
    public boolean match(SignActionEvent info) {
        return info.isType("stopwatch");
    }

    @Override
    public void execute(SignActionEvent info) {
        if (info.isTrainSign()
                && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)
                && info.isPowered() && info.hasGroup()
        ) {
            String name = Util.getCleanLine(info.getSign(), 2);
            if(name.isEmpty())
                return;

            TimeSign sign = getStopwatch(name);
            sign.trigger();
            sign.update();
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create()
                .setName(event.isCartSign() ? "cart stopwatch" : "train stopwatch")
                .setDescription("Stopwatch")
                .handle(event.getPlayer());
    }

    public static void updateAll() {
        for (TimeSign t : timerSigns.values()) {
            if (!t.update()) {
                return;
            }
        }
    }

    public static class TimeSign {
        public long startTime = -1;
        private final String name;

        public TimeSign(String name) {
            this.name = name;
        }

        public void trigger() {
            this.startTime = System.currentTimeMillis();
        }

        public String getStopwatch() {
            long elapsed = System.currentTimeMillis() - this.startTime;
            return timeFormat.format(elapsed);
        }

        public boolean update() {
            if (!TCConfig.SignLinkEnabled) return false;
            Variables.get(this.name + 'S').set(getStopwatch());
            return true;
        }
    }
}
