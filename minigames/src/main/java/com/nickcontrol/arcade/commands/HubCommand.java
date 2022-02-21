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

package com.nickcontrol.arcade.commands;

import com.nickcontrol.core.account.Rank;
import com.nickcontrol.core.command.ncCommand;
import com.nickcontrol.core.proxy.ProxyManager;
import com.nickcontrol.core.proxy.redis.TransferRequest;
import com.nickcontrol.core.redis.pubsub.RedisCommandManager;
import com.nickcontrol.core.status.server.ServerGroup;
import com.nickcontrol.core.status.server.ServerStatus;
import com.nickcontrol.core.status.server.ServerStatusManager;
import com.nickcontrol.core.utils.C;
import org.bukkit.entity.Player;

import java.util.Comparator;

public class HubCommand extends ncCommand {
    @Override
    public String commandName() {
        return "hub";
    }

    @Override
    public String[] alias() {
        return new String[]{"lobby"};
    }

    @Override
    public Rank rankNeeded() {
        return Rank.MEMBER;
    }

    @Override
    public Rank[] specificRanks() {
        return new Rank[0];
    }

    @Override
    public void execute(Player player, String[] args)
    {
        ProxyManager.Instance.sendToGroup(player, "Lobby");
    }
}
