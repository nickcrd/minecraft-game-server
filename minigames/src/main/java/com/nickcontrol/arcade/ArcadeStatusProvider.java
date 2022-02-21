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

package com.nickcontrol.arcade;

import com.nickcontrol.core.status.server.ServerStatus;
import com.nickcontrol.core.status.server.ServerStatusManager.ServerStatusProvider;

public class ArcadeStatusProvider extends ServerStatusProvider
{
    @Override
    public void update(ServerStatus status)
    {
        status.game = (GameManager.Instance.GameInstance == null) ? "None" : GameManager.Instance.GameInstance.getGamemode().name();
        status.map = (GameManager.Instance.GameInstance == null) ? "N/A" : GameManager.Instance.GameInstance.getMapData().name;
        status.state = GameManager.Instance.getState().name();
    }
}
