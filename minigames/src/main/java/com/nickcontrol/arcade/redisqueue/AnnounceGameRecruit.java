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

import com.nickcontrol.core.redis.pubsub.redisCommand;

public class AnnounceGameRecruit extends redisCommand
{
    private String game;
    private String server;

    public AnnounceGameRecruit(String game, String server) {
        this.game = game;
        this.server = server;
    }

    public String getGame() {
        return game;
    }

    public String getServer() {
        return server;
    }

    @Override
    public boolean isTarget() {
        return false;
    }

    @Override
    public void execute()
    {
        // Hub & Bot execution only
    }
}
