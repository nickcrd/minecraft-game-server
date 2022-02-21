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

import co.nickcontrolstudios.services.commons.redis.RedisManager;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashMap;

@Singleton
public class RedisCommandManager
{
    @Inject RedisManager redisManager;
    @Inject RedisCommandListener redisCommandListener;

    @Inject @Named("redis_password")
    private String redisPassword;

    public HashMap<String, Class<? extends redisCommand>> commands = new HashMap<>();

    private Gson gson = new Gson();

    public void enable()
    {
        Jedis jedis = redisManager.getJedisPool().getResource();
        jedis.auth(redisPassword);

        Thread thread = new Thread("Redis Manager")
        {
            public void run()
            {
                try
                {
                    jedis.psubscribe(redisCommandListener, "redis_command:*");
                }
                catch (JedisConnectionException e)
                {
                    e.printStackTrace();
                    redisManager.getJedisPool().returnBrokenResource(jedis);
                }
                finally
                {
                    if (redisManager.getJedisPool() != null)
                        redisManager.getJedisPool().returnResource(jedis);
                }
            }
        };

        thread.start();
    }

    public void sendCommand(redisCommand command)
    {
        new Thread(() -> {
            Jedis jedis = redisManager.getJedisPool().getResource();
            jedis.auth(redisPassword);

            try
            {
                String type = command.getClass().getSimpleName();
                String data = gson.toJson(command);
                jedis.publish("redis_command:" + type, data);
            }
            catch (JedisConnectionException e)
            {
                e.printStackTrace();
                redisManager.getJedisPool().returnBrokenResource(jedis);
                jedis = null;
            }
            finally
            {
                if (redisManager.getJedisPool() != null)
                    redisManager.getJedisPool().returnResource(jedis);
            }
        }).start();
    }

    public void addCommand(Class<? extends redisCommand>... cmds)
    {
        for (Class<? extends redisCommand> cmd : cmds)
        {
            commands.put(cmd.getSimpleName(), cmd);
        }
    }


    public void onCommand(String commandType, String data)
    {
        if (!commands.containsKey(commandType))
            return;

        redisCommand command = gson.fromJson(data, commands.get(commandType));

        if (!command.isTarget())
            return;

        command.execute();
    }

}
