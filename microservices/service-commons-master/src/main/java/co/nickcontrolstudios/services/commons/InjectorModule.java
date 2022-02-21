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

package co.nickcontrolstudios.services.commons;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import java.util.Map;

public class InjectorModule extends AbstractModule {
    private Microservice microservice;
    private Map<String, String> properties;

    public InjectorModule(Microservice microservice, Map<String, String> properties) {
        this.microservice = microservice;
        this.properties = properties;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure()
    {
        bind(Microservice.class).toInstance(microservice);

        bind(String.class).annotatedWith(Names.named("mysql_url")).toInstance(properties.get("MYSQL_URL"));
        bind(String.class).annotatedWith(Names.named("mysql_db")).toInstance(properties.get("MYSQL_DB"));
        bind(String.class).annotatedWith(Names.named("mysql_user")).toInstance(properties.get("MYSQL_USER"));
        bind(String.class).annotatedWith(Names.named("mysql_password")).toInstance(properties.get("MYSQL_PASSWORD"));

        bind(String.class).annotatedWith(Names.named("redis_ip")).toInstance(properties.get("REDIS_IP"));
        bind(Integer.class).annotatedWith(Names.named("redis_port")).toInstance(Integer.parseInt(properties.get("REDIS_PORT")));
        bind(String.class).annotatedWith(Names.named("redis_password")).toInstance(properties.get("REDIS_PASSWORD"));
    }
}
