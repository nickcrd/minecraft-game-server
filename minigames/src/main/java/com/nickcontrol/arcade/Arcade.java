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

package com.nickcontrol.arcade;

import com.nickcontrol.arcade.commands.HubCommand;
import com.nickcontrol.arcade.commands.PlayAgainCommand;
import com.nickcontrol.arcade.debugcommands.DebugGamePlayer;
import com.nickcontrol.arcade.debugcommands.SetNextGameCommand;
import com.nickcontrol.arcade.debugcommands.SetStateCommand;
import com.nickcontrol.arcade.debugcommands.StartGameDebug;
import com.nickcontrol.arcade.game.GameState;
import com.nickcontrol.arcade.managers.*;
import com.nickcontrol.arcade.redisqueue.RequestGameResponseCommand;
import com.nickcontrol.core.ModuleManager;
import com.nickcontrol.core.account.AccountManager;
import com.nickcontrol.core.account.link.DiscordAccountLink;
import com.nickcontrol.core.antivpn.VPNDetect;
import com.nickcontrol.core.chat.ChatManager;
import com.nickcontrol.core.client.NICKCONTROLClientManager;
import com.nickcontrol.core.combat.CombatManager;
import com.nickcontrol.core.command.CommandManager;
import com.nickcontrol.core.cosmetics.CosmeticsManager;
import com.nickcontrol.core.economy.EconomyManager;
import com.nickcontrol.core.guard.Guard;
import com.nickcontrol.core.log.LogManager;
import com.nickcontrol.core.npc.NPCManager;
import com.nickcontrol.core.proxy.ProxyManager;
import com.nickcontrol.core.punish.Punish;
import com.nickcontrol.core.redis.RedisManager;
import com.nickcontrol.core.redis.pubsub.RedisCommandManager;
import com.nickcontrol.core.report.ReportsManager;
import com.nickcontrol.core.scoreboard.ScoreboardManager;
import com.nickcontrol.core.scoreboard.ncScoreboard;
import com.nickcontrol.core.stats.StatsManager;
import com.nickcontrol.core.status.PlayerStatusManager;
import com.nickcontrol.core.status.server.ServerStatusManager;
import com.nickcontrol.core.teleport.TeleportManager;
import com.nickcontrol.core.test.TestFlowManager;
import com.nickcontrol.core.twofa.TwoFactorAuthentificator;
import com.nickcontrol.core.utils.*;
import com.nickcontrol.core.vanish.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Arcade extends JavaPlugin implements Listener
{
    @Override
    public void onEnable()
    {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new UtilUpdate(), 0L, 1L);
        Bukkit.getPluginManager().registerEvents(this, this);

        ModuleManager.loadModules(
                AccountManager.Initialize(this),
                CommandManager.Initialize(this),

                new DatabaseManager(this),

                EconomyManager.Initialize(this),

                UtilCooldown.Initialize(this),
                UtilExplosion.Initialize(this),

                ChatManager.Initialize(this),
                StatsManager.Initialize(this),

                PlayerStatusManager.Initialize(this),
                TeleportManager.Initialize(this),

                RedisManager.Initialize(this),
                RedisCommandManager.Initialize(this),
                ProxyManager.Initialize(this),
                ServerStatusManager.Initialize(this),
                ScoreboardManager.Initialize(this),
                Punish.Initialize(this),

                ReportsManager.Initialize(this),

                CosmeticsManager.Initialize(this),

                NPCManager.Initialize(this),
                CombatManager.Initialize(this),

                LogManager.Initalize(this),

                Guard.Initialize(this),

                VanishManager.Initialize(this),
                TwoFactorAuthentificator.Initialize(this),
                VPNDetect.Initialize(this),

                DiscordAccountLink.Initialize(this),
                NICKCONTROLClientManager.Initialize(this),

                GameManager.Initialize(this),
                MapManager.Initialize(this),
                PlayerStateManager.Initialize(this),
                LobbyManager.Initialize(this),
                TeamKitManager.Initialize(this),
                CountdownManager.Initialize(this),
                GameRewardsManager.Initialize(this)
        );

        NameCache.Initalize();

        GameManager.Instance.updateState(GameState.IDLE);
        CommandManager.Instance.addCommand(
                new HubCommand(),
                new PlayAgainCommand(),
                new SetStateCommand(),
                new DebugGamePlayer(),
                new StartGameDebug(),
                new SetNextGameCommand()
        );

        ServerStatusManager.Instance.statusProvider = new ArcadeStatusProvider();

        Bukkit.getPluginManager().registerEvents(new TestFlowManager(), this);

        RedisCommandManager.Instance.addCommand(RequestGameResponseCommand.class);
    }


    @EventHandler
    public void onUpdate(TickEvent event) {
        if (event.getType() != TickType.FASTEST)
            return;

        for (Player player : ScoreboardManager.Instance.scoreboards.keySet()) {
            ncScoreboard sb = ScoreboardManager.Instance.scoreboards.get(player);

            if (sb.ColorMain != ChatColor.YELLOW) {
                sb.title = GameManager.Instance.GameInstance == null ? "  ARCADE GAMES  " : GameManager.Instance.GameInstance.getGamemode().getDisplayName().toUpperCase();
                sb.ColorMain = ChatColor.YELLOW;
                sb.ColorTrans = ChatColor.GOLD;
            }

            sb.clear();
            sb.write(C.Gray + UtilTime.date() + " " + ServerStatusManager.Instance.getServerName());

            if (!GameManager.Instance.getState().isLive()) {

                sb.writeNL();
                sb.write("Game: §e" + (GameManager.Instance.GameInstance == null ? "None" : GameManager.Instance.GameInstance.getGamemode().getDisplayName()));
                sb.writeNL();
                sb.write("Map: §b" + (GameManager.Instance.GameInstance == null ? "N/A" : GameManager.Instance.GameInstance.getMapData().name));
                sb.writeNL();

                String status = "§7...";
                String statusSecondary = null;
                switch (GameManager.Instance.getState()) {
                    case IDLE:
                        status = "§7Waiting for Game";
                        break;
                    case LOADING_GAME:
                        status = "§7Loading Game";
                        break;
                    case WAITING_FOR_PLAYERS:
                        if (GameManager.Instance.GameInstance.getCountdown() != -1) {
                            if (GameManager.Instance.GameInstance.getCountdown() <= 60) {
                                status = "§aStarting in " + GameManager.Instance.GameInstance.getCountdown() + "...";
                                break;
                            } else {
                                statusSecondary = "Starting in " + UtilTime.makeString(GameManager.Instance.GameInstance.getCountdown() * 1000);
                            }
                        }

                        status = "§aWaiting for Players";
                        if (GameManager.Instance.GameInstance.getPlayersAlive().size() < GameManager.Instance.GameInstance.getProperties().getMinimumPlayers())
                            statusSecondary = "§fWaiting for " + (GameManager.Instance.GameInstance.getProperties().getMinimumPlayers() - GameManager.Instance.GameInstance.getPlayersPlaying().size()) + " more players to join";
                        break;
                    case PREPARING_PLAYERS:
                    case GAME_IN_PROGRESS:
                        status = "§aGame in Progress...";
                        break;
                    case ENDING:
                        status = "§cGame Over";
                        break;
                }
                sb.write("Status: " + status);
                if (statusSecondary != null)
                    sb.write(statusSecondary);

                UtilActionBar.sendPersistant(sb.getPlayer(), status + (statusSecondary != null ? " §7- §f" + statusSecondary : ""));

                sb.writeNL();
                sb.write("Players: §7" + GameManager.Instance.GameInstance.getPlayersPlaying().size() + "/" + GameManager.Instance.GameInstance.getProperties().getMaximumPlayers());
                sb.writeNL();
                //  sb.write("Your Status: " + GameManager.Instance.GameInstance.getGamePlayers().get(sb.getPlayer()).getState().name());
                //   sb.writeNL();
            }
            else
            {
                GameManager.Instance.GameInstance.scoreboard(sb);
            }

            sb.update();
        }
    }

    public static void giveHubInventory(Player player)
    {
        player.getInventory().setItem(8, new ItemBuilder(Material.WATCH).setTitle(C.Red + "Return to Lobby §7(Right Click)").build());
        CosmeticsManager.Instance.giveChestItem(player);
    }

}
