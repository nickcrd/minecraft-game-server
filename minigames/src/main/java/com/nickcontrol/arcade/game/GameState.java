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

public enum GameState
{
    IDLE,
    LOADING_GAME,
    WAITING_FOR_PLAYERS,
    PREPARING_PLAYERS,
    GAME_IN_PROGRESS,
    ENDING,
    DEAD;

    public boolean isLive()
    {
        return this == PREPARING_PLAYERS || this == GAME_IN_PROGRESS;
    }
}
