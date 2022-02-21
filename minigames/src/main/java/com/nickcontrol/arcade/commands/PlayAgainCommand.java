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

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.game.GameState;
import com.nickcontrol.arcade.game.GameType;
import com.nickcontrol.arcade.redisqueue.RequestGameResponseCommand;
import com.nickcontrol.core.account.Rank;
import com.nickcontrol.core.command.ncCommand;
import com.nickcontrol.core.proxy.ProxyManager;
import com.nickcontrol.core.proxy.TransferEvent;
import com.nickcontrol.core.redis.pubsub.RedisCommandManager;
import com.nickcontrol.core.status.server.ServerStatus;
import com.nickcontrol.core.status.server.ServerStatusManager;
import com.nickcontrol.core.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PlayAgainCommand extends ncCommand {
    @Override
    public String commandName() {
        return "playagain";
    }

    @Override
    public String[] alias() {
        return new String[] { "nbg", "searchgame", "nextgame", "findgame"};
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
        if (GameManager.Instance.getState() != GameState.ENDING)
        {
            player.sendMessage(C.main("Game", "You cannot use this command at the moment."));
            return;
        }

        // Find Idle servers
        player.sendMessage(C.main("Transfer", "Looking for a §f" + GameManager.Instance.GameInstance.getGamemode().getDisplayName() + "§7 server to send you to."));

        ServerStatus serverStatus = ServerStatusManager.Instance.getServers().stream().filter(server ->
                server.game.equalsIgnoreCase(GameManager.Instance.GameInstance.getGamemode().name())).
                filter(server -> !GameManager.Instance.GameInstance.getProperties().isEnforceMaximumPlayers() || GameManager.Instance.GameInstance.getProperties().getMaximumPlayers() > server.players)
                .filter(server -> server.state.equals("WAITING_FOR_PLAYERS") || server.state.equals("PREPARING_PLAYERS") || (GameManager.Instance.GameInstance.getProperties().isJoinMidgame() && server.state.equals("GAME_IN_PROGRESS"))).findAny().orElse(null);

        if (serverStatus != null) {
            player.sendMessage(C.main("Transfer", "Sending you to " + C.item("§f" + serverStatus.serverName)));
            Bukkit.getPluginManager().callEvent(new TransferEvent(player, serverStatus.serverName));
            ProxyManager.Instance.doConnect(player.getUniqueId(), serverStatus);
            return;
        }

        serverStatus = ServerStatusManager.Instance.getServers().stream().filter(server -> server.state.equalsIgnoreCase("IDLE")).findAny().orElse(null);

        if (serverStatus != null) {

            player.sendMessage(C.main("Game", "Contacting server..."));
            RedisCommandManager.Instance.sendCommand(new RequestGameResponseCommand(serverStatus.serverName, GameManager.Instance.GameInstance.getGamemode(), GameType.DEFAULT));

            player.sendMessage(C.main("Transfer", "Sending you to " + C.item("§f" + serverStatus.serverName)));
            Bukkit.getPluginManager().callEvent(new TransferEvent(player, serverStatus.serverName));
            ProxyManager.Instance.doConnect(player.getUniqueId(), serverStatus);
            return;
        }
        player.sendMessage(C.main("Transfer", "There are currently no §f" + GameManager.Instance.GameInstance.getGamemode().getDisplayName() + "§7 server to send you to."));

    }
}
