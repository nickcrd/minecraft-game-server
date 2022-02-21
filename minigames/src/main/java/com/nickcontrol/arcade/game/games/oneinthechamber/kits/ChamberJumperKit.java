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

package com.nickcontrol.arcade.game.games.oneinthechamber.kits;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.arcade.game.GameState;
import com.nickcontrol.arcade.game.games.bowspleef.stats.DoubleJumpsStatsTracker;
import com.nickcontrol.arcade.kit.Kit;
import com.nickcontrol.arcade.kit.KitAvailability;
import com.nickcontrol.arcade.player.GamePlayer;
import com.nickcontrol.core.utils.*;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class ChamberJumperKit extends Kit {
    public ChamberJumperKit() {
        super("Jumper", KitAvailability.FREE, 0);
    }

    @Override
    public void applyKit(GamePlayer player) {
        player.getPlayer().getInventory().addItem(new ItemBuilder(Material.STONE_SWORD).build());
        player.getPlayer().getInventory().addItem(new ItemBuilder(Material.BOW).build());

        player.getPlayer().getInventory().setItem(8, new ItemBuilder(Material.ARROW).build());
    }

    @EventHandler
    public void doJump(PlayerToggleFlightEvent event)
    {
        Player player = event.getPlayer();

        if (GameManager.Instance.getState() != GameState.GAME_IN_PROGRESS)
            return;

        if (!GameManager.Instance.GameInstance.isAlive(event.getPlayer()))
            return;

        if (!isUsingKit(event.getPlayer()))
            return;

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
            return;

        event.setCancelled(true);
        player.setFlying(false);

        player.setAllowFlight(false);

        UtilAlg.velocity(player, player.getLocation().getDirection(), 1.4f, true, 0.4f, 0, 3f, true);

        player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 0);

        DoubleJumpsStatsTracker.Instance.trackStat(player);
    }

    @EventHandler
    public void updateDoubleJump(TickEvent event)
    {
        if (event.getType() != TickType.TICK)
            return;

        if (GameManager.Instance.getState() != GameState.GAME_IN_PROGRESS)
            return;

        for (Player player : UtilServer.getPlayers())
        {
            if (!GameManager.Instance.GameInstance.isAlive(player))
                continue;

            if (!isUsingKit(player))
                continue;

            if (player.isOnGround() && !player.getLocation().getBlock().getRelative(BlockFace.DOWN).isEmpty())
                player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void fallCancel(EntityDamageEvent event)
    {
        if (event.getEntity().getType() == EntityType.PLAYER && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (isUsingKit((Player) event.getEntity()))
                event.setCancelled(true);
        }
    }
}
