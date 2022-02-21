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
import com.google.inject.Injector;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Bootstrap
{
    public static String[] requiredProperties = new String[]{
            "SERVICE_NAME", "SERVICE_PORT"
    };

    public static Injector injector;

    public static void init(Microservice service, String[] rawArgs) throws Exception
    {
        List<String> args = Arrays.asList(rawArgs);

        if (args.contains("--debug"))
        {
            // Enable debug
            service.setDebug(true);
        }

        Map<String, String> properties;

        if (args.contains("--useconfig"))
        {
            // Use config instead of environment variables
            properties = Utils.parseConfig();
        }
        else
        {
            // Load properties from env variables
            properties = Arrays.stream(requiredProperties).collect(Collectors.toMap(prop -> prop, prop -> System.getenv(prop) == null ? "" : System.getenv(prop)));
        }

        InjectorModule module = new InjectorModule(service, properties);
        injector = module.createInjector();
        injector.injectMembers(service);

        service.start(properties);
    }
}
