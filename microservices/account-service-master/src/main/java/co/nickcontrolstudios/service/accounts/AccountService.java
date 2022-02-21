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

package co.nickcontrolstudios.service.accounts;

import co.nickcontrolstudios.service.accounts.repo.AccountRepository;
import co.nickcontrolstudios.services.commons.Microservice;
import co.nickcontrolstudios.services.commons.response.GenericApiResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import spark.Route;

import java.util.UUID;

import static spark.Spark.*;

public class AccountService extends Microservice {
        
    @Inject
    AccountRepository accountRepository;

    private Gson GSON = new Gson();

    @Override
    public void onStart() {
        accountRepository.init();
    }

    @Override
    public void bindRoutes() {
        // Register endpoints
        path("/accounts", () -> {
            get("/get/:uuid", getAccountData(), GSON::toJson);
            get("/exists/:uuid", getAccountExists(), GSON::toJson);

            post("/add", createAccount(), GSON::toJson);

            path("/update", () -> {
                post("/:uuid/name", updateName(), GSON::toJson);
                post("/:uuid/rank", updateRank(), GSON::toJson);
            });
        });

    }

    public Route getAccountData()
    {
        return (req, res) -> accountRepository.getPlayerData(UUID.fromString(req.params(":uuid")));
    }
    public Route getAccountExists()
    {
        return (req, res) -> accountRepository.getAccountExists(UUID.fromString(req.params(":uuid")));
    }

    public Route createAccount()
    {
        return (req, res) -> {
            if (req.body() == null)     return new GenericApiResponse(false, "No body retrieved");

            JsonObject body = GSON.fromJson(req.body(), JsonObject.class);

            if (body == null)      return new GenericApiResponse(false, "Invalid JSON data");

            return accountRepository.createAccount(body);
        };
    }

    public Route updateName()
    {
        return (req, res) -> {
            if (req.body() == null)     return new GenericApiResponse(false, "No body retrieved");

            JsonObject body = GSON.fromJson(req.body(), JsonObject.class);

            if (body == null)      return new GenericApiResponse(false, "Invalid JSON data");

            return accountRepository.updateName(UUID.fromString(req.params(":uuid")), body);
        };
    }

    public Route updateRank()
    {
        return (req, res) -> {
            if (req.body() == null)     return new GenericApiResponse(false, "No body retrieved");

            JsonObject body = GSON.fromJson(req.body(), JsonObject.class);

            if (body == null)      return new GenericApiResponse(false, "Invalid JSON data");

            return accountRepository.updateRank(UUID.fromString(req.params(":uuid")), body);
        };
    }
}
