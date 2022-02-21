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

package com.nickcontrol.arcade.player;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.game.GameState;
import com.nickcontrol.arcade.kit.Kit;
import com.nickcontrol.arcade.team.Team;
import com.nickcontrol.core.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GamePlayer
{
    private Player player;
    private PlayerState state;
    private Team team;
    private Kit kit;

    private HashMap<String, Integer> statsTracker = new HashMap<>();
    private int diamondsEarned = 0;

    public GamePlayer(Player player)
    {
        this.player = player;
    }

    public PlayerState getState()
    {
        return state;
    }

    public Player getPlayer()
    {
        return player;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setTeam(Team team, boolean announce)
    {
        setTeam(team);
        if (announce)
            player.sendMessage(C.main("Team", "You joined " + C.item(team.getTeamColor() + "§l" + team.getDisplayName())));
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit, boolean announce)
    {
        if (this.kit != null)
        {
            this.kit.onDeselect(this);
        }

        this.kit = kit;

        kit.onSelect(this);

        if (announce)
        {
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 2f, 1f);
            player.sendMessage(C.main("Kit", "You're now using " + C.item(kit.getDisplayName()) + "."));
        }

        if (GameManager.Instance.getState() == GameState.PREPARING_PLAYERS || GameManager.Instance.getState() == GameState.GAME_IN_PROGRESS)
            kit.applyKit(this);

    }

    public void setState(PlayerState newState)
    {
        setState(newState, true);
    }

    public void setState(PlayerState newState, boolean callEvent)
    {
        if (callEvent) {
            PlayerStateUpdateEvent event = new PlayerStateUpdateEvent(this, newState);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled())
                state = newState;
        }
        else
            state = newState;
    }

    public HashMap<String, Integer> getStatsTracker() {
        return statsTracker;
    }

    public void addStat(String statsName, int value)
    {
        int v = 0;
        if (statsTracker.containsKey(statsName))
            v = statsTracker.remove(statsName);

        statsTracker.put(statsName, v+value);
    }

    public int getDiamondsEarned() {
        return diamondsEarned;
    }

    public void clearRewards()
    {
        diamondsEarned = 0; statsTracker.clear();
    }

    public void addDiamondsEarned(int diamonds) {
        diamondsEarned += diamonds;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof GamePlayer)
            return (((GamePlayer) obj).player.getUniqueId().equals(this.player.getUniqueId()));
        else
            return false;
    }
}
