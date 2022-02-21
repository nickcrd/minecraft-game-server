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

package co.nickcontrolstudios.service.accounts.data;

import co.nickcontrolstudios.services.commons.redis.data.RedisData;
import lombok.Data;

import java.util.HashMap;
import java.util.UUID;

@Data
public class PlayerData implements RedisData {
    // Unique ID
    private String id;

    private UUID uuid;
    private String name;

    private String rank;
    private String[] secondaryRanks;

    private long firstJoined;
    private long lastSeen;

    public HashMap<String, Subscription> subscriptions;

    @Override
    public String getID() {
        return id;
    }
}
