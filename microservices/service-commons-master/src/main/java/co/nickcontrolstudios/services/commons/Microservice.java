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

package co.nickcontrolstudios.services.commons;

import co.nickcontrolstudios.services.commons.utils.Utils;
import com.google.gson.Gson;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static spark.Spark.*;

public abstract class Microservice
{
    @Getter private final Logger LOGGER = LoggerFactory.getLogger(Microservice.class);
    private Gson GSON;

    @Getter private boolean debug;

    @Getter private Map<String, String> properties;

    public void start(Map<String, String> properties) throws Exception
    {
        this.properties = properties;

        Utils.printInfo(LOGGER, properties.get("SERVICE_NAME"));

        // Set Port
        port((Integer.parseInt(properties.get("SERVICE_PORT"))));

        LOGGER.info(">> Binding to port " + properties.get("SERVICE_PORT"));

        after("/*", (req, res) -> res.type("application/json"));

        onStart();
        bindRoutes();
    }

    public void onStart() {}

    public abstract void bindRoutes();

    public void setDebug(boolean isDebug)
    {
        debug = isDebug;
        LOGGER.info("Debug Mode was " + (isDebug ? "ENABLED" : "DISABLED"));
    }

}
