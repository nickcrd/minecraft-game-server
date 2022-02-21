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

package com.nickcontrol.arcade.team;

import com.google.common.base.Objects;
import com.nickcontrol.arcade.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Team
{
    private String displayName;
    private ChatColor teamColor;
    private String teamSpawnName;
    private List<Location> teamSpawns;

    public Team(ChatColor teamColor, String teamSpawns) {
        this("", teamColor, teamSpawns);
    }

    public Team(String displayName, ChatColor teamColor, String teamSpawns) {
        this.displayName = displayName;
        this.teamColor = teamColor;
        this.teamSpawnName = teamSpawns;
    }

    public void initSpawns()
    {
        teamSpawns = GameManager.Instance.GameInstance.getMapData().getSpawns(teamSpawnName);
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getTeamColor() {
        return teamColor;
    }

    public List<Location> getTeamSpawns() {
        return teamSpawns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equal(displayName, team.displayName) &&
                teamColor == team.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(displayName, teamColor);
    }
}
