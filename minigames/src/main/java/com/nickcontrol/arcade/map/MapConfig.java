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

package com.nickcontrol.arcade.map;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapConfig
{
    public String name = "Loading...";
    public String author = "Loading...";

    public List<DataPoint> data;
    public List<DataPoint> spawns;
    public List<MapProperty> properties;

    public transient World world;

    public List<Location> getDataPoint(String name)
    {
        List<DataPoint> dataPoints = data.stream().filter(data -> data.name.equalsIgnoreCase(name)).collect(Collectors.toList());
        if (dataPoints == null || dataPoints.isEmpty())
        {
            return new ArrayList<>();
        }

        ArrayList<Location> locs = new ArrayList<>();

        for (DataPoint dataPoint : dataPoints)
        {
            dataPoint.locs.forEach(locw -> locs.add(new Location(world, locw.x, locw.y, locw.z)));
        }

        return locs;
    }

    public List<Location> getSpawns(String name)
    {
        List<DataPoint> dataPoints = spawns.stream().filter(data -> data.name.equalsIgnoreCase(name)).collect(Collectors.toList());
        if (dataPoints == null || dataPoints.isEmpty())
        {
            return new ArrayList<>();
        }

        ArrayList<Location> locs = new ArrayList<>();

        for (DataPoint dataPoint : dataPoints)
        {
            dataPoint.locs.forEach(locw -> locs.add(new Location(world, locw.x, locw.y, locw.z)));
        }

        return locs;
    }
}
