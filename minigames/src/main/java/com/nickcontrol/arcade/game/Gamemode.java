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

import com.nickcontrol.arcade.game.games.bowspleef.BowSpleef;
import com.nickcontrol.arcade.game.games.custom.CustomGame;
import com.nickcontrol.arcade.game.games.gladiators.Gladiators;
import com.nickcontrol.arcade.game.games.halloween.TheLastAmongUs;
import com.nickcontrol.arcade.game.games.miniwalls.MiniWalls;
import com.nickcontrol.arcade.game.games.oneinthechamber.OneInTheChamber;
import com.nickcontrol.arcade.game.games.skywars.Skywars;
import com.nickcontrol.arcade.game.games.tnttrouble.TNTTrouble;
import com.nickcontrol.arcade.game.games.towerdefense.TowerDefense;

public enum Gamemode
{
    TNT_TROUBLE(TNTTrouble.class, "TNT Trouble", GameType.DEFAULT),
    BOWSPLEEF(BowSpleef.class, "Bow Spleef", GameType.DEFAULT),
    ONE_IN_THE_CHAMBER(OneInTheChamber.class, "One in the Chamber", GameType.DEFAULT),
    GLADIATORS(Gladiators.class, "Gladiators", GameType.DEFAULT),
    MINI_WALLS(MiniWalls.class, "Mini Walls", GameType.DEFAULT),

    THE_LAST_AMONG_US(TheLastAmongUs.class, "The Last Among Us", GameType.DEFAULT),

    SKYWARS(Skywars.class, "Skywars", GameType.DEFAULT),
    TOWER_DEFENSE(TowerDefense.class, "Tower Defense", GameType.DEFAULT),

    CUSTOM(CustomGame.class, "Custom Game", GameType.DEFAULT)
    ;

    private Class<? extends Game> gameClass;
    private String displayName;
    private GameType[] typeModifiers;

    private Gamemode(Class<? extends Game> gameClass, String displayName, GameType... typeModifers)
    {
        this.gameClass = gameClass;
        this.displayName = displayName;
        this.typeModifiers = typeModifers;
    }

    public Class<? extends Game> getGameClass() {
        return gameClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public GameType[] getTypeModifiers() {
        return typeModifiers;
    }
}
