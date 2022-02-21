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

package co.nickcontrolstudios.service.accounts.repo;

import co.nickcontrolstudios.service.accounts.data.PlayerData;
import co.nickcontrolstudios.service.accounts.data.Subscription;
import co.nickcontrolstudios.services.commons.mysql.MySQLManager;
import co.nickcontrolstudios.services.commons.redis.data.RedisDataRepository;
import co.nickcontrolstudios.services.commons.response.GenericApiResponse;
import co.nickcontrolstudios.services.commons.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class AccountRepository
{
    @Inject
    MySQLManager mySQLManager;

    @Inject
    @Getter
    private RedisDataRepository<PlayerData> redisCache;

    private int CACHE_DURATION = 30 * 60; // in seconds

    private Gson GSON = new Gson();

    public void init() {
        redisCache.init(PlayerData.class, "playerData");
    }

    public Object getPlayerData(UUID uuid)
    {
        if (redisCache.elementExists(uuid.toString()))
        {
            return redisCache.getElement(uuid.toString());
        }

        try
        {
            PreparedStatement statement = mySQLManager.getConnection().prepareStatement("SELECT * FROM accounts WHERE uuid = ? LIMIT 1");
            statement.setString(1, uuid.toString());

            statement.executeQuery();


            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next())
            {
                PlayerData token = new PlayerData();
                token.setId(resultSet.getString("player_id"));
                token.setUuid(UUID.fromString(resultSet.getString("uuid")));
                token.setName(resultSet.getString("name"));
                token.setRank(resultSet.getString("rank"));
                token.setSecondaryRanks(GSON.fromJson(resultSet.getString("secondaryRanks"), String[].class));
                token.subscriptions = new HashMap<>();

                // Add Subscriptions
                PreparedStatement prep = mySQLManager.getConnection().prepareStatement("SELECT * FROM subscriptions WHERE uuid = ?");
                prep.setString(1, uuid.toString());

                prep.executeQuery();

                ResultSet rs2 = prep.getResultSet();

                while (rs2.next())
                {
                    Subscription s = new Subscription();

                    s.type = rs2.getString("type");
                    s.datePurchased = rs2.getLong("datePurchased");
                    s.expireAfter = rs2.getLong("expireAfter");

                    token.subscriptions.put(s.type, s);
                }

                redisCache.addElement(token, CACHE_DURATION);
                return token;

            }

            return new GenericApiResponse(false, "No data found");
        }
        catch (Exception e)
        {
            return new GenericApiResponse(false, e.getMessage());
        }
    }

    public Object getAccountExists(UUID uuid)
    {
        if (redisCache.elementExists(uuid.toString()))
        {
            return true;
        }

        try {
            PreparedStatement statement = mySQLManager.getConnection().prepareStatement("SELECT * FROM accounts WHERE uuid = ? LIMIT 1");
            statement.setString(1, uuid.toString());

            statement.executeQuery();


            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next())
            {
                return GSON.fromJson("{\"exists\":true}", JsonObject.class);
            }

            return GSON.fromJson("{\"exists\":false}", JsonObject.class);
        }
        catch (Exception e)
        {
            return new GenericApiResponse(false, e.getMessage());
        }

    }

    public Object updateName(UUID uuid, JsonObject body)
    {
        if (!body.has("name")) return new GenericApiResponse(false, "'name' has not been specified");
//        if (!body.has("uuid")) return new GenericResponse(false, "'uuid' has not been specified");


        try {
            PreparedStatement statement = mySQLManager.getConnection().prepareStatement("UPDATE accounts SET userName = ? WHERE uuid = ?;");

            statement.setString(1, body.get("name").getAsString());
            statement.setString(2, uuid.toString());

            statement.executeUpdate();

            redisCache.removeElement(uuid.toString());

            return new GenericApiResponse(true, "Updated account with values " + body);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new GenericApiResponse(false, "SQL Exception occurred while trying to update account: " + e.getMessage());
        }
    }

    public Object updateRank(UUID uuid, JsonObject body) {
        if (!body.has("rank")) return new GenericApiResponse(false, "'rank' has not been specified");
        if (!body.has("secondaryRanks")) return new GenericApiResponse(false, "'secondaryRanks' has not been specified");

        try {
            PreparedStatement statement = mySQLManager.getConnection().prepareStatement("UPDATE accounts SET rank = ?, subRanks = ? WHERE uuid = ?;");

            statement.setString(1, body.get("rank").getAsString());
            statement.setString(2, body.get("secondaryRanks").toString());
            statement.setString(3, uuid.toString());

            statement.executeUpdate();

            redisCache.removeElement(uuid.toString());

            return new GenericApiResponse(true, "Updated account with values " + body);
        } catch (Exception e) {
            e.printStackTrace();
            return new GenericApiResponse(false, "SQL Exception occurred while trying to update account: " + e.getMessage());
        }
    }

    public Object createAccount(JsonObject body)
    {
        if (!body.has("name"))  return new GenericApiResponse(false, "'name' has not been specified");
        if (!body.has("uuid"))  return new GenericApiResponse(false, "'uuid' has not been specified");

        try
        {
            PreparedStatement statement = mySQLManager.getConnection().prepareStatement("INSERT INTO accounts (player_id, name, uuid, rank, secondaryRanks) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            String id = Utils.getId();

            statement.setString(1, id);
            statement.setString(2, body.get("name").getAsString());
            statement.setString(3, body.get("uuid").getAsString());
            statement.setString(4, "MEMBER");
            statement.setString(5, "[]");

            statement.executeUpdate();

            PlayerData data = new PlayerData();

            data.setId(id);
            data.setUuid(UUID.fromString(body.get("uuid").getAsString()));
            data.setName(body.get("name").getAsString());
            data.setRank("MEMBER");
            data.setSecondaryRanks(new String[0]);
            data.setSubscriptions(new HashMap<>());
            data.setLastSeen(System.currentTimeMillis());
            data.setFirstJoined(System.currentTimeMillis());

            redisCache.addElement(data, CACHE_DURATION);
            return data;

            //return new GenericApiResponse(false, "Something went wrong creating user account (No generated keys returned)");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new GenericApiResponse(false, "SQL Exception occurred while trying to create account: " + e.getMessage());
        }
    }
}
