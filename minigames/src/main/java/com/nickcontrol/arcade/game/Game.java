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

package com.nickcontrol.arcade.game;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.kit.Kit;
import com.nickcontrol.arcade.managers.MapManager;
import com.nickcontrol.arcade.map.MapConfig;
import com.nickcontrol.arcade.player.GamePlayer;
import com.nickcontrol.arcade.player.PlayerState;
import com.nickcontrol.arcade.stats.*;
import com.nickcontrol.arcade.team.Team;
import com.nickcontrol.core.scoreboard.ncScoreboard;
import com.nickcontrol.core.utils.C;
import com.nickcontrol.core.utils.UtilServer;
import com.nickcontrol.core.utils.UtilTitle;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Game implements Listener {
    private GameProperties gameProperties;
    private String mapPreference;
    private Gamemode gamemode;
    private GameType gameType;
    private MapConfig mapData;
    public List<Team> teamList = new ArrayList<>();
    public List<Kit> kitList = new ArrayList<>();

    private List<StatsTracker> statsTrackers = new ArrayList<>();

    private boolean countdownForce = false;
    private int countdown = -1;

    private HashMap<UUID, GamePlayer> gamePlayers = new HashMap<>();

    private List<GamePlayer> _winners;

    public boolean advertisedGame = false;

    public Game(Gamemode gamemode, GameType type) {
        this.gamemode = gamemode;
        this.gameType = type;
        gameProperties = new GameProperties();

        addStatsTracker(new DamageDealtStatsTracker(), new DamageTakenStatsTracker(), new DeathsStatsTracker(), new GamesPlayedStatsTracker(), new KillsStatsTracker(), new WinStatsTracker());
    }

    public void onGameLoad() {
        File map = MapManager.Instance.getMap(gamemode, gameType, mapPreference);
        mapData = MapManager.Instance.initializeMap(this, map);
        GameManager.Instance.GameInstance.teamList.forEach(team -> {team.initSpawns();});
        // TODO: Load Gameplayers
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (GameManager.Instance.isSpectator(player))
                continue;

            GamePlayer gamePlayer = new GamePlayer(player);
            gamePlayer.setState(PlayerState.ALIVE, false);
            // TODO: Set Kit etc
            gamePlayers.put(player.getUniqueId(), gamePlayer);
        }
    }

    public void addTeams(Team... team)
    {
        teamList.addAll(Arrays.asList(team));
    }

    public void addKits(Kit... kits)
    {
        kitList.addAll(Arrays.asList(kits));
    }

    public void addStatsTracker(StatsTracker... trackers)
    {
        statsTrackers.addAll(Arrays.asList(trackers));
    }

    public abstract void onGameStart();

    public abstract void preparePlayer(GamePlayer player);

    public GameProperties getProperties() {
        return gameProperties;
    }

    public void setGameProperties(GameProperties p) {
        gameProperties = p;
    }

    public abstract void winCheck();

    public void announceWinner(Team winningTeam)
    {

        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 2f, 1f);
            player.sendMessage("");
            player.sendMessage(C.center("§6§lThanks for playing §e§l" + getGamemode().getDisplayName()));
            player.sendMessage("");
            player.sendMessage(C.center(C.White + C.Bold + "Winner: §f" + winningTeam.getTeamColor() + C.Bold + winningTeam.getDisplayName() + " Team"));
            player.sendMessage("");

            // TODO: Apply reward?
            if (getGamePlayer(player) != null && getGamePlayer(player).getTeam().equals(winningTeam))
            {
                UtilTitle.display("§a§lYOU WON!", winningTeam.getTeamColor() + C.Bold + winningTeam.getDisplayName()+ "§f won the game!", 20, 60, 20, player);
            }
            else
            {
                UtilTitle.display("§c§lGAME OVER!", winningTeam.getTeamColor() + C.Bold + winningTeam.getDisplayName()+ "§f won the game!", 20, 60, 20, player);
            }
        }

        getTeamPlayers(winningTeam).forEach(player1 -> player1.addDiamondsEarned(50));

        GameManager.Instance.updateState(GameState.ENDING);

    }

    public void announceWinner(List<List<GamePlayer>> winners)
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            String winnersString = "";

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 2f, 1f);
            player.sendMessage("");
            player.sendMessage(C.center("§6§lThanks for playing §e§l" + getGamemode().getDisplayName()));
            player.sendMessage("");

            if (winners.size() >= 1) {
                StringJoiner first = new StringJoiner(", ");

                _winners = winners.get(0);

                winners.get(0).forEach(player1 -> first.add(player1.getPlayer().getName()));

                player.sendMessage(C.center(C.Red + C.Bold + "1st Place: §f" + first.toString()));

                winnersString = first.toString();

                if (getGamePlayer(player) != null && winners.get(0).contains(getGamePlayer(player)))
                {
                    UtilTitle.display("§a§lYOU WON!", C.Gold + first + "§f won the game!", 20, 60, 20, player);
                }
            }
            if (winners.size() >= 2) {
                StringJoiner second = new StringJoiner(", ");

                winners.get(1).forEach(player1 -> second.add(player1.getPlayer().getName()));

                player.sendMessage(C.center(C.Gold + C.Bold + "2nd Place: §f" + second.toString()));
                if (getGamePlayer(player) != null && winners.get(1).contains(getGamePlayer(player)))
                {
                    UtilTitle.display("§6§l2ND PLACE!", C.Gold + winnersString + "§f won the game!", 20, 60, 20, player);
                }

            }
            if (winners.size() >= 3) {
                StringJoiner third = new StringJoiner(", ");

                winners.get(2).forEach(player1 -> third.add(player1.getPlayer().getName()));

                player.sendMessage(C.center(C.Yellow + C.Bold + "3rd Place: §f" + third.toString()));

                if (getGamePlayer(player) != null && winners.get(1).contains(getGamePlayer(player)))
                {
                    UtilTitle.display("§e§l3RD PLACE!", C.Gold + winnersString + "§f won the game!", 20, 60, 20, player);
                }
            }

            if (winners.isEmpty())
            {
                player.sendMessage(C.center(C.Gray + "No one won the game."));
                UtilTitle.display("§c§lGAME OVER!", "§7No one won the game.", 20, 60, 20, player);
            }
            player.sendMessage("");


           /* UtilTitle.display(, C.White + "won the game!", 20, 40, 20, UtilServer.getPlayers());*/

        }


        if (winners.size() >= 1) winners.get(0).forEach(player1 -> player1.addDiamondsEarned(50));

        if (winners.size() >= 2) winners.get(1).forEach(player1 -> player1.addDiamondsEarned(30));

        if (winners.size() >= 3) winners.get(2).forEach(player1 -> player1.addDiamondsEarned(15));



        GameManager.Instance.updateState(GameState.ENDING);

    }

    public List<GamePlayer> getWinners()
    {
        return _winners;
    }

    public String getMapPreference() {
        return mapPreference;
    }

    public void setMapPreference(String mapPreference) {
        this.mapPreference = mapPreference;
    }

    public Gamemode getGamemode() {
        return gamemode;
    }

    public GameType getGameType() {
        return gameType;
    }

    public MapConfig getMapData() {
        return mapData;
    }

    public List<Team> getTeams() {
        return teamList;
    }

    public List<Kit> getKits() {
        return kitList;
    }

    public List<StatsTracker> getStatsTrackers()
    {
        return statsTrackers;
    }

    public boolean isCountdownForce() {
        return countdownForce;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public void setCountdownForce(boolean countdownForce) {
        this.countdownForce = countdownForce;
    }

    public HashMap<UUID, GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public GamePlayer getGamePlayer(Player player)
    {
        return gamePlayers.get(player.getUniqueId());
    }

    public void addGamePlayer(GamePlayer player)
    {
        gamePlayers.put(player.getPlayer().getUniqueId(), player);
    }

    public boolean canJoinMidGame(Player player)
    {
        return getProperties().isJoinMidgame();
    }

    public List<GamePlayer> getPlayersAlive()
    {
        return (List<GamePlayer>) gamePlayers.values().stream().filter(gp -> (gp.getState() == PlayerState.ALIVE)).collect(Collectors.toList());
    }

    public List<GamePlayer> getPlayersPlaying()
    {
        return (List<GamePlayer>) gamePlayers.values().stream().filter(gp -> (gp.getState() != PlayerState.SPECTATOR)).collect(Collectors.toList());
    }

    public List<GamePlayer> getTeamPlayers(Team team, boolean aliveOnly)
    {
        return gamePlayers.values().stream().filter(gp -> gp.getTeam().equals(team)).filter(gp -> {
            if (aliveOnly)
                return gp.getState() == PlayerState.ALIVE;
            else
                return true;
        }).collect(Collectors.toList());
    }

    public List<GamePlayer> getTeamPlayers(Team team)
    {
        return gamePlayers.values().stream().filter(gp -> gp.getTeam().equals(team)).collect(Collectors.toList());
    }


    public boolean isAlive(Player player)
    {
        GamePlayer gamePlayer = gamePlayers.get(player.getUniqueId());
        if (gamePlayer == null)
            return false;

       return gamePlayer.getState() == PlayerState.ALIVE;
    }

    public abstract void scoreboard(ncScoreboard scoreboard);
}
