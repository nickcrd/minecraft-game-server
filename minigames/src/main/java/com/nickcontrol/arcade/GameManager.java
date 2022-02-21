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

import com.google.gson.Gson;
import com.nickcontrol.arcade.event.GameStateChangedEvent;
import com.nickcontrol.arcade.game.Game;
import com.nickcontrol.arcade.game.GameState;
import com.nickcontrol.arcade.game.GameType;
import com.nickcontrol.arcade.game.Gamemode;
import com.nickcontrol.arcade.game.games.custom.CustomGameConfig;
import com.nickcontrol.arcade.player.GamePlayer;
import com.nickcontrol.core.Module;
import com.nickcontrol.core.proxy.TransferEvent;
import com.nickcontrol.core.proxy.redis.TransferRequest;
import com.nickcontrol.core.redis.pubsub.RedisCommandManager;
import com.nickcontrol.core.status.server.ServerGroup;
import com.nickcontrol.core.status.server.ServerStatus;
import com.nickcontrol.core.status.server.ServerStatusManager;
import com.nickcontrol.core.utils.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.nickcontrol.arcade.game.GameState.DEAD;
import static com.nickcontrol.arcade.game.GameState.ENDING;

public class GameManager extends Module
{
    public static GameManager Instance;

    public static GameManager Initialize(JavaPlugin plugin) {
        Instance = new GameManager(plugin);
        return Instance;
    }

    private GameState state = GameState.IDLE;
    private Long stateTime = System.currentTimeMillis();

    private ArrayList<Player> specList = new ArrayList<>();

    public Game GameInstance;

    private Gamemode[] partyGames = new Gamemode[] { Gamemode.TNT_TROUBLE/*, Gamemode.PAINTBALL*/ };

    // For /game set
    public Gamemode nextGame;
    public GameType nextGameType = null;


    protected GameManager(JavaPlugin plugin) {
        super("Game Manager", plugin);

    }

    @Override
    public void onEnable() {

    }

    public GameState getState() {
        return state;
    }

    public Long getStateTime() {
        return stateTime;
    }

    public void updateState(GameState newState)
    {
        System.out.println("[GAME] Game state was updated to " + newState);
        state = newState;
        stateTime = System.currentTimeMillis();
        Bukkit.getPluginManager().callEvent(new GameStateChangedEvent(state, newState));
    }

    public void loadNewGame(Gamemode mode, GameType type)
    {
        if (mode == Gamemode.CUSTOM)
        {
            // ----
            // CUSTOM GAMEMODE
            // ----
            try
            {
                String json = "";
                for (String line : Files.readAllLines(new File("/home/nickcontrol/custom-game.txt").toPath()))
                    json += line;

                CustomGameConfig config = new Gson().fromJson(json, CustomGameConfig.class);

                Game game = mode.getGameClass().getConstructor(CustomGameConfig.class).newInstance(config);
                GameInstance = game;

                Bukkit.getPluginManager().registerEvents(GameInstance, getPlugin());

                updateState(GameState.LOADING_GAME);
            }
            catch (NoSuchMethodException ex)
            {
                System.err.println("ERROR >>> CANNOT CREATE NEW GAME INSTANCE!");
                ex.printStackTrace();

                Bukkit.shutdown();
                return;
            }
            catch (InvocationTargetException ex)
            {
                ex.getCause().printStackTrace();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return;
            }

            return;


        }


        try
        {
            Game game = mode.getGameClass().getConstructor(GameType.class).newInstance(type);
            GameInstance = game;

            Bukkit.getPluginManager().registerEvents(GameInstance, getPlugin());

            updateState(GameState.LOADING_GAME);
        }
        catch (NoSuchMethodException ex)
        {
            System.err.println("ERROR >>> CANNOT CREATE NEW GAME INSTANCE!");
            ex.printStackTrace();

            Bukkit.shutdown();
            return;
        }
		catch (InvocationTargetException ex)
        {
            ex.getCause().printStackTrace();
        }
		catch (Exception ex)
        {
            ex.printStackTrace();
            return;
        }
    }

    @EventHandler
    public void cleanUp(TickEvent event)
    {
        if (event.getType() != TickType.FAST)
            return;

        if (GameInstance != null && getState() == DEAD && UtilTime.elapsed(getStateTime(), 1000))
        {
            HandlerList.unregisterAll(GameInstance);

            GameInstance.getKits().forEach(HandlerList::unregisterAll);

            GameInstance.getStatsTrackers().forEach(statsTracker -> statsTracker.disable());

            if (GameInstance.getMapData().world != null) {
                WorldHelper.unloadWorldAndClearReferences(getPlugin(), GameInstance.getMapData().world);
                WorldHelper.clearWorldDirectory(GameInstance.getMapData().world.getName());
            }
            GameInstance = null;

            updateState(GameState.IDLE);
        }
    }

    @EventHandler
    public void onIdle(GameStateChangedEvent event)
    {
        if (event.getNewState() == GameState.IDLE)
        {
            // TODO: Do Party Games check

            // Next game was already determined by the master server or manually set by admin:
            if (nextGame != null)
            {
                loadNewGame(nextGame, (nextGameType != null ? nextGameType : GameType.DEFAULT));
                return;
            }
        }
    }

    @EventHandler
    public void onGameLoad(GameStateChangedEvent event)
    {
        if (event.getNewState() == GameState.LOADING_GAME)
        {
            GameInstance.onGameLoad();
        }
    }

    @EventHandler
    public void preparePlayers(GameStateChangedEvent event)
    {
        if (event.getNewState() != GameState.PREPARING_PLAYERS)
            return;

        for (int i=0 ; i<GameInstance.getPlayersAlive().size() ; i++)
        {
            GamePlayer player = GameInstance.getPlayersAlive().get(i);

            Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), () -> {

                UtilPlayer.clear(player.getPlayer());
                GameManager.Instance.GameInstance.preparePlayer(player);

                Location spawn = UtilAlg.getSpawnLocation(player.getTeam().getTeamSpawns(), Arrays.asList(UtilServer.getPlayers()));
                if (spawn == null && player.getTeam().getTeamSpawns().size() >= 1)
                    spawn = player.getTeam().getTeamSpawns().get(0);

                if (spawn != null)
                    player.getPlayer().teleport(spawn);

                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.LEVEL_UP, 2f, 1f);

                if (player.getKit() != null)
                    player.getKit().applyKit(player);

                player.getPlayer().sendMessage("");
                player.getPlayer().sendMessage("");
                player.getPlayer().sendMessage("");
                player.getPlayer().sendMessage(C.center("§6§lGame §f- §e§l" + GameInstance.getGamemode().getDisplayName()));
                player.getPlayer().sendMessage("");
                for (String line : GameInstance.getProperties().getDescription()) {
                    player.getPlayer().sendMessage(C.center(C.White + line));
                }

                player.getPlayer().sendMessage(" ");

            }, i);
        }
    }

    @EventHandler
    public void announceGameEnd(GameStateChangedEvent event)
    {
        if (event.getNewState() != ENDING)
            return;

        for (int i=0 ; i<UtilServer.getPlayers().length ; i++)
        {
            Player player = UtilServer.getPlayers()[i];

            Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), () -> {
                player.sendMessage(" ");
                //player.sendMessage(C.center("§e§lThanks for playing §6§l" + GameManager.Instance.GameInstance.getGamemode().getDisplayName()));

                player.spigot().sendMessage(new ComponentBuilder(C.getSpacingCenter("§a§lPLAY AGAIN §fOR §c§lRETURN TO LOBBY")).append("PLAY AGAIN").color(ChatColor.GREEN).bold(true)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/playagain")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick Here §7to play again!").create())).
                                append(" or ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.WHITE).bold(false)
                        .append("RETURN TO LOBBY").color(ChatColor.RED).bold(true)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hub")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cClick Here §7to return to lobby!").create())).
                                create());

                player.sendMessage(" ");
            }, i+40);
        }
    }

    @EventHandler
    public void teleportBackToLobby(GameStateChangedEvent event)
    {
        if (event.getNewState() != DEAD)
            return;

        for (int i=0 ; i<UtilServer.getPlayers().length ; i++)
        {
            Player player = UtilServer.getPlayers()[i];

            Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), (Runnable) () -> {

                UtilPlayer.clear(player);
                Arcade.giveHubInventory(player);

                ServerGroup group = ServerStatusManager.Instance.getServerGroups().stream().filter(g -> (g.getName().equalsIgnoreCase("LOBBY"))).findAny().orElse(null);

                if (group != null)
                {
                    int slots = 1; // TODO Required slots for party

                    ServerStatus bestGroupServer = ServerStatusManager.Instance.repo.getElements().stream().filter(serverStatus -> (serverStatus.serverGroup.equalsIgnoreCase(group.getName()))).sorted(new Comparator<ServerStatus>() {
                        @Override
                        public int compare(ServerStatus a, ServerStatus b) {
                            // TODO Might need to do checks for game in progress servers

                            if (a.maxPlayers - a.players < slots && b.maxPlayers - b.players >= slots)
                                return -1;

                            if (b.maxPlayers - b.players < slots && a.maxPlayers - a.players >= slots)
                                return 1;

                            if (a.players > b.players)
                                return -1;

                            if (b.players > a.players)
                                return 1;

                            return 0;
                        }
                    }).findAny().orElse(null);

                    if (bestGroupServer == null)
                    {
                        player.kickPlayer("Game is over! Type /lobby to return to lobby.");
                        return;
                    }

                    Bukkit.getPluginManager().callEvent(new TransferEvent(player, bestGroupServer.serverName));
                    RedisCommandManager.Instance.sendCommand(new TransferRequest(player.getUniqueId(), bestGroupServer.serverName));
                    return;
                }

                player.kickPlayer("Game is over! Type /lobby to return to lobby.");

            }, i);
        }
    }

    @EventHandler
    public void endCheck(TickEvent event)
    {
        if (event.getType() != TickType.FASTER)
            return;

        if (getState() != GameState.GAME_IN_PROGRESS)
            return;

        if (GameInstance == null)
            return;

        GameInstance.winCheck();
    }

    private boolean isSpectator(Player player)
    {
        return specList.contains(player);
    }
}
