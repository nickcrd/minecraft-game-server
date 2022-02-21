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
import com.nickcontrol.arcade.event.GameStateChangedEvent;
import com.nickcontrol.arcade.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class WinStatsTracker extends StatsTracker
{
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onGameStart(GameStateChangedEvent event)
    {
        if (event.getNewState() != GameState.ENDING)
            return;

        if (GameManager.Instance.GameInstance.getWinners() == null || GameManager.Instance.GameInstance.getWinners().isEmpty())
            return;

        GameManager.Instance.GameInstance.getWinners().forEach(gp -> {
            applyStat(gp, "Wins", 1, false);
        });
    }
}
