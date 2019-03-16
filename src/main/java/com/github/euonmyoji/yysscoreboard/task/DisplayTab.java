package com.github.euonmyoji.yysscoreboard.task;

import com.github.euonmyoji.yysscoreboard.YysScoreBoard;
import com.github.euonmyoji.yysscoreboard.configuration.PluginConfig;
import com.github.euonmyoji.yysscoreboard.data.TabData;
import com.github.euonmyoji.yysscoreboard.util.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.List;

/**
 * @author yinyangshi
 */
public class DisplayTab implements Runnable {
    private final List<TabData> data;
    private int index = 0;
    private volatile boolean running;

    public DisplayTab(List<TabData> data) {
        this.data = data;
        running = !data.isEmpty();
    }

    @Override
    public void run() {
        if (running) {
            Task.Builder builder = Task.builder().execute(this);
            try {
                builder.delayTicks(data.get(index).delay);
                Util.getStream(Sponge.getServer().getOnlinePlayers()).forEach(data.get(index)::setTab);
                if (++index >= data.size()) {
                    index = 0;
                }
                if (PluginConfig.asyncTab) {
                    builder.async();
                }
            } catch (Throwable e) {
                YysScoreBoard.logger.warn("something wrong", e);
            }
            builder.submit(YysScoreBoard.plugin);
        }
    }

    public void setPlayer(Player p) {
        if (running) {
            int i = index;
            if (i >= data.size()) {
                i = 0;
            }
            data.get(i).setTab(p);
        }
    }

    public void cancel() {
        running = false;
    }
}
