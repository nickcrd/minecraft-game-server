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

package co.nickcontrolstudios.services.commons.mysql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public class MySQLManager
{
    private DataSource dataSource;
    private Connection connection;

    @Inject
    public MySQLManager(@Named("mysql_url") String server, @Named("mysql_db") String db, @Named("mysql_user") String user, @Named("mysql_password")String pass)
    {
        dataSource = openDataSource("jdbc:mysql://" + server + "/" + db, user, pass);
    }

    private static DataSource openDataSource(String url, String username, String password)
    {
        BasicDataSource source = new BasicDataSource();
        source.addConnectionProperty("autoReconnect", "true");
        source.addConnectionProperty("allowMultiQueries", "true");
        source.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl(url);
        source.setUsername(username);
        source.setPassword(password);
        source.setMaxTotal(4);
        source.setMaxIdle(4);
        source.setTimeBetweenEvictionRunsMillis(180 * 1000);
        source.setSoftMinEvictableIdleTimeMillis(180 * 1000);

        return source;
    }

    public void connect() throws SQLException {
        connection = dataSource.getConnection();
    }

    public Connection getConnection() throws SQLException {

        if (connection == null)
        {
            connect();
            return connection;
        }

        if (connection.isValid(0))
            return connection;

        return dataSource.getConnection();
    }
}
