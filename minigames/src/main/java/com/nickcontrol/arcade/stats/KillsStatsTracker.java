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

package com.nickcontrol.arcade.stats;

import com.nickcontrol.arcade.GameManager;
import com.nickcontrol.core.combat.CombatDeathEvent;
import com.nickcontrol.core.utils.ItemBuilder;
import com.nickcontrol.core.utils.UtilPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class KillsStatsTracker extends StatsTracker
{
    @EventHandler
    public void onDeath(CombatDeathEvent event) {
        Player attacker = UtilPlayer.searchOnline(event.getEntity(), event.getLastDamage().Attacker, false);

        if (attacker == null)
            return;

        if (GameManager.Instance.GameInstance == null)
            return;

        if (GameManager.Instance.GameInstance.getGamePlayer(attacker) == null)
            return;

        applyStat(GameManager.Instance.GameInstance.getGamePlayer(attacker), "Kills", 1, false);
    }
}
