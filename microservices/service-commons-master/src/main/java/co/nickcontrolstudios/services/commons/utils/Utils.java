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

package co.nickcontrolstudios.services.commons.utils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Utils
{
    private static Hashids hashids = new Hashids("", 12, "1234567890abcdef");

    public static void printInfo(Logger logger, String moduleName)
    {
        logger.info(" ");
        logger.info(" ");
        logger.info("            .oosss+.  /oooso-          ");
        logger.info("           -m.    -h+sy`    `yo          ");
        logger.info("          `d/       +d`      oy        ===== MICROSERVICE =====");
        logger.info("          oy                 so         >>  " + moduleName + "  <<");
        logger.info("         -m`                :h`        ");
        logger.info("        `d/                `m-               nickcontrol.ch");
        logger.info("        oy                 yo          ");
        logger.info("       -m`      `-        /d          (c) 2019 NICKCONTROL Studios.");
        logger.info("       :h`      ym/      .m-              All rights reserved.  ");
        logger.info("        :d-    /d`o+    `yo          ");
        logger.info("         -soo+++`  -+++++-");
        logger.info(" ");
        logger.info(" ");
        logger.info(">> Loading microservice '" + moduleName + "'");
    }

    public static Map<String, String> parseConfig() throws IOException
    {
        File config = new File("service_config");
        if (!config.exists())
            throw new IllegalStateException("service_config file doesn't exist");

        return Files.lines(Paths.get(config.getPath())).collect(Collectors.toMap(
                line -> line.split(":", 1)[0],
                line -> line.split(":", 1)[1]
        ));
    }

    public static String getId(int id)
    {
        return hashids.encode(id);
    }

    public static String getId() {
        return getId(new Random().nextInt(Integer.MAX_VALUE));
    }
}
