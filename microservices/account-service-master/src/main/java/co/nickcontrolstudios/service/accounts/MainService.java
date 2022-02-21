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

package co.nickcontrolstudios.service.accounts;

import co.nickcontrolstudios.services.commons.Bootstrap;

public class MainService
{
    public static void main(String[] args) throws Exception
    {
        Bootstrap.requiredProperties = new String[] { "SERVICE_NAME", "SERVICE_PORT", "MYSQL_URL", "MYSQL_DB", "MYSQL_USER", "MYSQL_PASSWORD", "REDIS_IP", "REDIS_PORT", "REDIS_PASSWORD" };
        Bootstrap.init(new AccountService(), args);
    }
}
