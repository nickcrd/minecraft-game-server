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

package com.nickcontrol.arcade.game.games.bowspleef.kits;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.game.GameState;
import com.nickcontrol.arcade.kit.Kit;
import com.nickcontrol.arcade.kit.KitAvailability;
import com.nickcontrol.arcade.player.GamePlayer;
import com.nickcontrol.core.utils.ItemBuilder;
import com.nickcontrol.core.utils.UtilAlg;
import com.nickcontrol.core.utils.UtilCooldown;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class ArcherKit extends Kit
{
    public ArcherKit() {
        super("Archer", KitAvailability.FREE, 0);
    }

    @Override
    public void applyKit(GamePlayer player) {
        player.getPlayer().getInventory().addItem(new ItemBuilder(Material.BOW).addEnchantment(Enchantment.ARROW_FIRE, 1).setTitle("§eLeft Click§f to use Triple Shot").addEnchantment(Enchantment.ARROW_INFINITE, 1).build());
        player.getPlayer().getInventory().setItem(27, new ItemBuilder(Material.ARROW).build());
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event)
    {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        if (GameManager.Instance.getState() != GameState.GAME_IN_PROGRESS)
            return;

        if (!isUsingKit(event.getPlayer()))
            return;

        if (!GameManager.Instance.GameInstance.isAlive(event.getPlayer()))
            return;

        if (!UtilCooldown.Instance.addCooldown(event.getPlayer(), "Triple Shot", 1500, true, true))
            return;

        Vector totheRight = new Vector(0, 0, 0.2);

        Location location = event.getPlayer().getLocation();
        totheRight = UtilAlg.rotateVector(totheRight, location.getYaw(), location.getPitch());

        event.getPlayer().launchProjectile(Arrow.class, event.getPlayer().getLocation().getDirection()).setFireTicks(Integer.MAX_VALUE);
        event.getPlayer().launchProjectile(Arrow.class, event.getPlayer().getLocation().getDirection().clone().add(totheRight)).setFireTicks(Integer.MAX_VALUE);
        event.getPlayer().launchProjectile(Arrow.class, event.getPlayer().getLocation().getDirection().clone().add(totheRight.multiply(-1))).setFireTicks(Integer.MAX_VALUE);

    }
}
