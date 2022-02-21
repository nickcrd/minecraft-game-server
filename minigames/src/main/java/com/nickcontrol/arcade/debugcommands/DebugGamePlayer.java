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

import com.google.gson.Gson;
import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.core.account.Rank;
import com.nickcontrol.core.command.ncCommand;
import com.nickcontrol.core.utils.C;
import org.bukkit.entity.Player;

public class DebugGamePlayer extends ncCommand {
    @Override
    public String commandName() {
        return "gpdebug";
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
        player.sendMessage("Your kit is: " + GameManager.Instance.GameInstance.getGamePlayer(player).getKit().getIdentifier());
    }
}
