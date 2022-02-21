/*
 *
 * Copyright (c) 2020 NICKCONTROL Studios. All rights reserved.
 *
 * This file/repository is proprietary code. You are expressly prohibited from disclosing, publishing,
 * reproducing, or transmitting the content, or substantially similar content, of this repository, in whole or in part,
 * in any form or by any means, verbal or written, electronic or mechanical, for any purpose.
 * By browsing the content of this file/repository, you agree not to disclose, publish, reproduce, or transmit the content,
 * or substantially similar content, of this file/repository, in whole or in part, in any form or by any means, verbal or written,
 * electronic or mechanical, for any purpose.
 *
 */

package co.nickcontrolstudios.services.commons.redis.pubsub;

import com.google.inject.Inject;
import redis.clients.jedis.JedisPubSub;

public class RedisCommandListener extends JedisPubSub
{
    @Inject
    RedisCommandManager redisCommandManager;

    @Override
    public void onPMessage(String pattern, String channelName, String message)
    {
        try
        {
            String commandType = channelName.split(":")[1];
            redisCommandManager.onCommand(commandType, message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String channelName, String message)
    {

    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels)
    {

    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels)
    {

    }

    @Override
    public void onSubscribe(String channelName, int subscribedChannels)
    {

    }

    @Override
    public void onUnsubscribe(String channelName, int subscribedChannels)
    {

    }
}
