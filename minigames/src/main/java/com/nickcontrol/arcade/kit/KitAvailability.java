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

import org.bukkit.ChatColor;

public enum KitAvailability {
    FREE(ChatColor.WHITE),
    PAID(ChatColor.AQUA),
    ACHIEVEMENT(ChatColor.LIGHT_PURPLE);

    private ChatColor displayColor;
    KitAvailability(ChatColor color)
    {
        this.displayColor = color;
    }

    public ChatColor getDisplayColor() {
        return displayColor;
    }
}
