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

package com.nickcontrol.arcade.game.games.tnttrouble.kit;

import com.nickcontrol.arcade.kit.Kit;
import com.nickcontrol.arcade.kit.KitAvailability;
import com.nickcontrol.arcade.player.GamePlayer;
import com.nickcontrol.core.utils.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BomberKit extends Kit
{
    public BomberKit() {
        super("Bomber", KitAvailability.FREE, 0);
    }

    @Override
    public void applyKit(GamePlayer player)
    {
        player.getPlayer().getInventory().addItem(new ItemBuilder(Material.TNT).setTitle(C.Red + "Throwable TNT").build());
    }
}
