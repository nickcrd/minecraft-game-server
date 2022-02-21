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

package com.nickcontrol.arcade.game.games.bowspleef;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.game.Game;
import com.nickcontrol.arcade.game.GameState;
import com.nickcontrol.arcade.game.GameType;
import com.nickcontrol.arcade.game.Gamemode;
import com.nickcontrol.arcade.game.games.bowspleef.kits.ArcherKit;
import com.nickcontrol.arcade.game.games.bowspleef.kits.JumperKit;
import com.nickcontrol.arcade.game.games.bowspleef.kits.RepulsorKit;
import com.nickcontrol.arcade.game.games.bowspleef.stats.ArrowsFiredStatsTracker;
import com.nickcontrol.arcade.game.games.bowspleef.stats.DoubleJumpsStatsTracker;
import com.nickcontrol.arcade.game.games.bowspleef.stats.PlayersRepulsedStatsTracker;
import com.nickcontrol.arcade.player.GamePlayer;
import com.nickcontrol.arcade.player.PlayerState;
import com.nickcontrol.arcade.player.PlayerStateUpdateEvent;
import com.nickcontrol.arcade.team.Team;
import com.nickcontrol.core.scoreboard.ncScoreboard;
import com.nickcontrol.core.utils.C;
import com.nickcontrol.core.utils.UtilTime;
import com.nickcontrol.core.utils.UtilTitle;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BowSpleef extends Game
{
    private ArrayList<List<GamePlayer>> placements = new ArrayList<>();

    public BowSpleef(GameType type)
    {
        super(Gamemode.BOWSPLEEF, type);

        getProperties().setFallDamage(false);
        getProperties().setJoinMidgame(false);
        getProperties().setFrozenWhilePrepare(false);
        getProperties().setPvpDamage(false);

        getProperties().setMinimumPlayers(2);
        getProperties().setRecommendedPlayers(4);
        getProperties().setMaximumPlayers(16);
        getProperties().setEnforceMaximumPlayers(false);

        getProperties().setDescription(
                "Use your §bBow§f to make §cTNT fall §fbeneath the others",
                "§a§lJumper Kit: §fTap space twice to use §eDouble Jump",
                "§c§lArcher Kit: §fLeft-Click to use §cTriple Shot",
                "§b§lRepulsor Kit: §bSneak §fto §bknock§f nearby players away",
                "Last player alive wins!");

        getProperties().setDoRespawn(false);

        addTeams(new Team("Players", ChatColor.YELLOW,"Yellow"));
        addKits(new JumperKit(), new ArcherKit(), new RepulsorKit());

        addStatsTracker(new ArrowsFiredStatsTracker(), new DoubleJumpsStatsTracker(), new PlayersRepulsedStatsTracker());
    }

    @Override
    public void onGameStart() {

    }

    @Override
    public void preparePlayer(GamePlayer player) {

    }

    @Override
    public void winCheck()
    {
        if (getPlayersPlaying().size() == 0)
        {
            GameManager.Instance.updateState(GameState.DEAD);
            return;
        }

        if (getPlayersPlaying().size() == 1)
        {
            ArrayList<List<GamePlayer>> winners = new ArrayList<>();
            winners.add(0, new ArrayList<>());
            winners.get(0).add(getPlayersPlaying().get(0));

            announceWinner(winners);
            return;
        }

        if (getPlayersAlive().size() <= 1)
        {
            List<GamePlayer> playersAlive = new ArrayList<>();
            for (GamePlayer gp : getPlayersAlive())
                playersAlive.add(gp);

            placements.add(0, playersAlive);

            announceWinner(placements);
        }
    }

    @EventHandler
    public void onDeath(PlayerStateUpdateEvent event)
    {
        if (event.getState() == PlayerState.DEAD)
        {
            placements.add(0, Arrays.asList(event.getGamePlayer()));
        }
    }

    @Override
    public void scoreboard(ncScoreboard sb)
    {
        if (GameManager.Instance.getState() == GameState.GAME_IN_PROGRESS)
        {
            sb.writeNL();
            sb.write("Time Elapsed: §a" + UtilTime.makeString(System.currentTimeMillis()-GameManager.Instance.getStateTime(), 0, UtilTime.TimeUnit.AUTO, false));
        }

        sb.writeNL();
        sb.write("Players Alive:");
        getPlayersAlive().forEach(gp -> sb.write(C.Green + gp.getPlayer().getName()));
        sb.writeNL();
    }
}
