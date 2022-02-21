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

package com.nickcontrol.arcade.kit;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.player.GamePlayer;
import com.nickcontrol.core.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Kit implements Listener
{
    private String kitName;
    private KitAvailability kitAvailability;
    private String identifier;
    private int cost;
    private String[] description;

    public Kit(String kitName, KitAvailability kitAvailability, int cost, String... description) {
        this.kitName = kitName;
        this.identifier = kitName.replace(" ", "-");
        this.kitAvailability = kitAvailability;
        this.cost = cost;
        this.description = description;

        Bukkit.getPluginManager().registerEvents(this, GameManager.Instance.getPlugin());
    }

    public String getDisplayName()
    {
        return kitAvailability.getDisplayColor() + C.Bold + kitName + " Kit";
    }

    public abstract void applyKit(GamePlayer player);

    public void onSelect(GamePlayer player)
    {

    }

    public void onDeselect(GamePlayer player)
    {

    }

    public boolean isUsingKit(GamePlayer player)
    {
        return player.getKit().identifier.equalsIgnoreCase(identifier);
    }

    public boolean isUsingKit(Player player)
    {
        GamePlayer gp = GameManager.Instance.GameInstance.getGamePlayer(player);
        if (gp == null)
            return false;
        return gp.getKit().identifier.equalsIgnoreCase(identifier);
    }


    public String getKitName() {
        return kitName;
    }

    public KitAvailability getKitAvailability() {
        return kitAvailability;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getCost() {
        return cost;
    }

    public String[] getDescription() {
        return description;
    }
}
