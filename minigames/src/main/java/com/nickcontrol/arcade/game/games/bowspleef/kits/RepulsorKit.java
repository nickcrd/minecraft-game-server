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
import com.nickcontrol.arcade.game.games.bowspleef.stats.PlayersRepulsedStatsTracker;
import com.nickcontrol.arcade.kit.Kit;
import com.nickcontrol.arcade.kit.KitAvailability;
import com.nickcontrol.arcade.player.GamePlayer;
import com.nickcontrol.core.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class RepulsorKit extends Kit
{
    public RepulsorKit() {
        super("Repulsor", KitAvailability.FREE, 0);
    }

    @Override
    public void applyKit(GamePlayer player) {
        player.getPlayer().getInventory().addItem(new ItemBuilder(Material.BOW).addEnchantment(Enchantment.ARROW_FIRE, 1).addEnchantment(Enchantment.ARROW_INFINITE, 1).build());
        player.getPlayer().getInventory().setItem(27, new ItemBuilder(Material.ARROW).build());
    }

    @EventHandler
    public void onShift(PlayerToggleSneakEvent event)
    {
        if (!event.isSneaking())
            return;

        if (GameManager.Instance.getState() != GameState.GAME_IN_PROGRESS)
            return;

        if (!isUsingKit(event.getPlayer()))
            return;

        if (!GameManager.Instance.GameInstance.isAlive(event.getPlayer()))
            return;

        if (!UtilCooldown.Instance.addCooldown(event.getPlayer(), "Repulsor", 5000, true, true))
            return;

        Player player = event.getPlayer();

        player.sendMessage(C.main("Game", "You used §fRepulsor§7."));

        for (Player other : Bukkit.getOnlinePlayers())
        {
            if (player.equals(other))
                continue;

            int range = 3;

            if (UtilMath.offset(other, player) > range)
                continue;

            PlayersRepulsedStatsTracker.Instance.trackStat(player);

            UtilAlg.velocity(other, UtilAlg.getTrajectory2d(player, other), 1.6, true, 0.8, 0, 10, true);
            other.getWorld().playSound(other.getLocation(), Sound.CHICKEN_EGG_POP, 2f, 0.5f);
        }
    }
}
