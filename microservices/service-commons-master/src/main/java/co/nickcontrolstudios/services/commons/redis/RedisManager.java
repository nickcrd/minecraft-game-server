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

package co.nickcontrolstudios.services.commons.redis;

import co.nickcontrolstudios.services.commons.redis.pubsub.RedisCommandManager;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Singleton
public class RedisManager
{
    @Getter
    private JedisPool jedisPool;

    public Gson gson = new Gson();

    @Inject
    public RedisManager(@Named("redis_ip") String redisIp, @Named("redis_port")int redisPort, @Named("redis_password") String redisPassword)
    {
        createJedisPool(redisIp, redisPort);
    }

    public JedisPool createJedisPool(String redisIp, int redisPort)
    {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPool = new JedisPool(jedisPoolConfig, redisIp, redisPort);
        return jedisPool;
    }
}
