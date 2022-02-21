/*
 *
 * Copyright (c) 2019 NICKCONTROL. All rights reserved.
 *
 * This file/repository is proprietary code. You are expressly prohibited from disclosing, publishing,
 * reproducing, or transmitting the content, or substantially similar content, of this repository, in whole or in part,
 * in any form or by any means, verbal or written, electronic or mechanical, for any purpose.
 * By browsing the content of this file/repository, you agree not to disclose, publish, reproduce, or transmit the content,
 * or substantially similar content, of this file/repository, in whole or in part, in any form or by any means, verbal or written,
 * electronic or mechanical, for any purpose.
 *
 */

package com.nickcontrol.arcade.stats;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.game.Game;
import com.nickcontrol.arcade.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class StatsTracker implements Listener
{
    public StatsTracker()
    {
        Bukkit.getPluginManager().registerEvents(this, GameManager.Instance.getPlugin());
    }

    public void applyStat(GamePlayer player, String statsName, int amount, boolean global)
    {
       player.addStat((global ? "Global." : "") + statsName, amount);
    }

    public void disable()
    {
        HandlerList.unregisterAll(this);
    }
}
