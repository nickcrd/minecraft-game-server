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

package co.nickcontrolstudios.services.commons.redis.data;

import co.nickcontrolstudios.services.commons.redis.RedisManager;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.*;

public class RedisDataRepository<T extends RedisData>
{
    private RedisManager redisManager;
    private String redisPassword;

    private Class<T> elementType;
    private String elementLabel;

    @Inject
    public RedisDataRepository(RedisManager redisManager, @Named("redis_password") String redisPassword)
    {
        this.redisManager = redisManager;
        this.redisPassword = redisPassword;
    }

    public void init(Class<T> elementType, String elementLabel)
    {
        this.elementType = elementType;
        this.elementLabel = elementLabel;
    }

    public String getKey(String elementID)
    {
        return "data." + elementLabel + "." + elementID;
    }

    public Collection<T> getElements()
    {
        return getElements(getActiveElements());
    }

    public Collection<T> getElements(Collection<String> ids)
    {
        if (ids.isEmpty())
            return new HashSet<>();

        Collection<T> elements = new HashSet<T>();
        Jedis jedis = redisManager.getJedisPool().getResource();
        jedis.auth(redisPassword);

        try
        {
            Pipeline pipeline = jedis.pipelined();

            List<Response<String>> responses = new ArrayList<Response<String>>();
            for (String id : ids)
            {
                responses.add(pipeline.get(getKey(id)));
            }

            // Block until all requests have received pipelined responses
            pipeline.sync();

            for (Response<String> response : responses)
            {
                String serializedData = response.get();
                T element = redisManager.gson.fromJson(serializedData, elementType);

                if (element != null)
                {
                    elements.add(element);
                }
            }
        }
        catch (JedisConnectionException e)
        {
            e.printStackTrace();
            redisManager.getJedisPool().returnBrokenResource(jedis);
            jedis = null;
        }
        finally
        {
            if (jedis != null)
                redisManager.getJedisPool().returnResource(jedis);
        }

        return elements;
    }

    public T getElement(String dataId)
    {
        T element = null;
        Jedis jedis = redisManager.getJedisPool().getResource();
        jedis.auth(redisPassword);
        try
        {
            String key = getKey(dataId);
            String serializedData = jedis.get(key);
            element = redisManager.gson.fromJson(serializedData, elementType);
        }
        catch (JedisConnectionException e)
        {
            e.printStackTrace();
            redisManager.getJedisPool().returnBrokenResource(jedis);
            jedis = null;
        }
        finally
        {
            if (jedis != null)
                redisManager.getJedisPool().returnResource(jedis);
        }

        return element;
    }

    public void addElement(T element, int timeout)
    {
        Jedis jedis = redisManager.getJedisPool().getResource();
        jedis.auth(redisPassword);

        try
        {
            String serializedData = redisManager.gson.toJson(element);
            String dataId = element.getID();
            String dataKey = getKey(element.getID());
            long expiry = currentTime() + timeout;

            Transaction transaction = jedis.multi();
            transaction.set(dataKey, serializedData);
            transaction.zadd("data." + elementLabel, expiry, dataId);
            transaction.exec();
        }
        catch (JedisConnectionException e)
        {
            e.printStackTrace();
            redisManager.getJedisPool().returnBrokenResource(jedis);
            jedis = null;
        }
        finally
        {
            if (jedis != null)
                redisManager.getJedisPool().returnResource(jedis);
        }
    }

    public void addElement(T element)
    {
        addElement(element, 60 * 60 * 24 * 7 * 4 * 12 * 10);
    }

    public void removeElement(T element)
    {
        removeElement(element.getID());
    }

    public void removeElement(String dataId)
    {
        Jedis jedis = redisManager.getJedisPool().getResource();
        jedis.auth(redisPassword);

        try
        {
            String dataKey = getKey(dataId);

            Transaction transaction = jedis.multi();
            transaction.del(dataKey);
            transaction.zrem("data." + elementLabel, dataId);
            transaction.exec();
        }
        catch (JedisConnectionException exception)
        {
            exception.printStackTrace();
            redisManager.getJedisPool().returnBrokenResource(jedis);
            jedis = null;
        }
        finally
        {
            if (jedis != null)
            {
                redisManager.getJedisPool().returnResource(jedis);
            }
        }
    }

    public boolean elementExists(String dataId)
    {
        return getElement(dataId) != null;
    }

    public int clean()
    {
        Jedis jedis = redisManager.getJedisPool().getResource();
        jedis.auth(redisPassword);

        try
        {
            for (String dataId : getDeadElements())
            {
                String dataKey = getKey(dataId);

                Transaction transaction = jedis.multi();
                transaction.del(dataKey);
                transaction.zrem("data." + elementLabel, dataId);
                transaction.exec();
            }
        }
        catch (JedisConnectionException e)
        {
            e.printStackTrace();
            redisManager.getJedisPool().returnBrokenResource(jedis);
            jedis = null;
        }
        finally
        {
            if (jedis != null)
                redisManager.getJedisPool().returnResource(jedis);
        }

        return 0;
    }

    public void refreshElement(String dataId, int timeout)
    {
        Jedis jedis = redisManager.getJedisPool().getResource();
        jedis.auth(redisPassword);

        try
        {
            String dataKey = getKey(dataId);
            long expiry = currentTime() + timeout;

            Transaction transaction = jedis.multi();
            transaction.zrem("data." + elementLabel, dataId);
            transaction.zadd("data." + elementLabel, expiry, dataId);
            transaction.exec();
        }
        catch (JedisConnectionException exception)
        {
            exception.printStackTrace();
            redisManager.getJedisPool().returnBrokenResource(jedis);
            jedis = null;
        }
        finally
        {
            if (jedis != null)
                redisManager.getJedisPool().returnResource(jedis);
        }

    }

    public Set<String> getActiveElements()
    {
        Set<String> dataIds = new HashSet<>();
        Jedis jedis = redisManager.getJedisPool().getResource();
        jedis.auth(redisPassword);

        try
        {
            String min = "(" + currentTime();
            String max = "+inf";
            dataIds = jedis.zrangeByScore("data." + elementLabel, min, max);
        }
        catch (JedisConnectionException exception)
        {
            exception.printStackTrace();
            redisManager.getJedisPool().returnBrokenResource(jedis);
            jedis = null;
        }
        finally
        {
            if (jedis != null)
            {
                redisManager.getJedisPool().returnResource(jedis);
            }
        }

        return dataIds;
    }

    public Set<String> getDeadElements()
    {
        Set<String> dataIds = new HashSet<String>();
        Jedis jedis = redisManager.getJedisPool().getResource();
        jedis.auth(redisPassword);

        try
        {
            String min = "-inf";
            String max = currentTime() + "";
            dataIds = jedis.zrangeByScore("data." + elementLabel, min, max);
        }
        catch (JedisConnectionException exception)
        {
            exception.printStackTrace();
            redisManager.getJedisPool().returnBrokenResource(jedis);
            jedis = null;
        }
        finally
        {
            if (jedis != null)
            {
                redisManager.getJedisPool().returnResource(jedis);
            }
        }

        return dataIds;
    }

    private long currentTime()
    {
        long currentTime = 0;
        Jedis jedis = redisManager.getJedisPool().getResource();
        jedis.auth(redisPassword);

        try
        {
            currentTime = Long.parseLong(jedis.time().get(0));
        }
        catch (JedisConnectionException exception)
        {
            exception.printStackTrace();
            redisManager.getJedisPool().returnBrokenResource(jedis);
            jedis = null;
        }
        finally
        {
            if (jedis != null)
            {
                redisManager.getJedisPool().returnResource(jedis);
            }
        }

        return currentTime;
    }

}