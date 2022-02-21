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

package com.nickcontrol.arcade.redisqueue;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.game.GameState;
import com.nickcontrol.arcade.game.GameType;
import com.nickcontrol.arcade.game.Gamemode;
import com.nickcontrol.core.redis.pubsub.RedisCommandManager;
import com.nickcontrol.core.redis.pubsub.redisCommand;
import com.nickcontrol.core.status.server.ServerStatusManager;
import org.bukkit.Bukkit;

/*
    Manually send request using....
    
    publish redis_command:RequestGameResponseCommand {\"gamemode\":\"ONE_IN_THE_CHAMBER\",\"gametype\":\"DEFAULT\",\"serverName\":\"miniTEST\"}
    publish redis_command:RequestGameResponseCommand {\"gamemode\":\"GLADIATORS\",\"gametype\":\"DEFAULT\",\"serverName\":\"miniTEST\"}
*/
public class RequestGameResponseCommand extends redisCommand
{
    private Gamemode gamemode;
    private GameType gametype = GameType.DEFAULT;

    private String serverName;

    public RequestGameResponseCommand(String serverName, Gamemode gamemode) {
        this.serverName = serverName;
        this.gamemode = gamemode;
    }

    public RequestGameResponseCommand(String serverName,Gamemode gamemode, GameType gametype) {
        this.serverName = serverName;
        this.gamemode = gamemode;
        this.gametype = gametype;
    }

    @Override
    public boolean isTarget() {
        return ServerStatusManager.Instance.getServerName().equalsIgnoreCase(serverName);
    }

    @Override
    public void execute()
    {
        if (GameManager.Instance.getState() == GameState.IDLE)
        {
            Bukkit.getScheduler().runTask(RedisCommandManager.Instance.getPlugin(), () -> {
                System.out.println("[GAME MANAGER] Loading new game from Master Server request.");
                GameManager.Instance.loadNewGame(gamemode, gametype);
            });
        }
        else
        {
            System.out.println("[GAME MANAGER] Received game request from Master Server, but status is not IDLE..");
        }
    }

}
