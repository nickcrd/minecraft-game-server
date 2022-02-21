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

public class GamesPlayedStatsTracker extends StatsTracker
{
    @EventHandler
    public void onGameStart(GameStateChangedEvent event)
    {
        if (event.getNewState() != GameState.GAME_IN_PROGRESS)
            return;

        GameManager.Instance.GameInstance.getPlayersPlaying().forEach(gp -> {
            applyStat(gp, "GamesPlayed", 1, false);
            applyStat(gp, "GamesPlayed", 1, true);
        });
    }
}
