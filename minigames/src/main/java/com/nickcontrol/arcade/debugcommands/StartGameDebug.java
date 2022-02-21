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

package com.nickcontrol.arcade.debugcommands;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.managers.CountdownManager;
import com.nickcontrol.core.account.Rank;
import com.nickcontrol.core.command.ncCommand;
import org.bukkit.entity.Player;

public class StartGameDebug extends ncCommand
{
    @Override
    public String commandName() {
        return "startgame";
    }

    @Override
    public String[] alias() {
        return new String[0];
    }

    @Override
    public Rank rankNeeded() {
        return Rank.DEV;
    }

    @Override
    public Rank[] specificRanks() {
        return new Rank[0];
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (args == null) {
            CountdownManager.Instance.doCountdown(GameManager.Instance.GameInstance, 10, true);
        }
        else
        {
            CountdownManager.Instance.doCountdown(GameManager.Instance.GameInstance, Integer.parseInt(args[0]), true);
        }
    }
}
