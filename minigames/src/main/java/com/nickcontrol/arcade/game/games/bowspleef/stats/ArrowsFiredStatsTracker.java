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

package com.nickcontrol.arcade.game.games.bowspleef.stats;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.stats.StatsTracker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ArrowsFiredStatsTracker extends StatsTracker
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onShoot(ProjectileLaunchEvent event)
    {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        Player player = (Player) event.getEntity().getShooter();

        if (GameManager.Instance.GameInstance.getGamePlayer(player) == null)
            return;

        applyStat(GameManager.Instance.GameInstance.getGamePlayer(player), "ArrowsFired", 1, false);
    }
}
