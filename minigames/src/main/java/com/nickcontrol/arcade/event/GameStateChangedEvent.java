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

package com.nickcontrol.arcade.event;

import com.nickcontrol.arcade.game.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private GameState from;
    private GameState to;

    public GameStateChangedEvent(GameState from, GameState to) {
        this.from = from;
        this.to = to;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GameState getNewState()
    {
        return to;
    }

    public GameState getOldState()
    {
        return from;
    }
}

