package com.njdaeger.projectmanager.dataaccess.impl;

import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.dataaccess.IDataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlDataAccess implements IDataAccess {

    private Connection database;
    private final String prefix;
    private final ProjectManager plugin;

    public SqlDataAccess(ProjectManager plugin) {
        this.prefix = plugin.getPMConfig().getDatabasePrefix();

    }

    private Connection getConnection() {
        var config = plugin.getPMConfig();

        try {
            if (this.database == null || this.database.isClosed()) {
                this.database = DriverManager.getConnection(
                        "jdbc:mysql://" +
                                config.getDatabaseHost() + ":" +
                                config.getDatabasePort() + "/" +
                                config.getDatabaseName() + "?allowReconnect=true&autoReconnect=true",
                        config.getDatabaseUsername(),
                        config.getDatabasePassword());
            }
            return this.database;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Unable to connect to database.");
    }

    @Override
    public void initializeDatabase() {

    }

    @Override
    public int update(String query) {
        return 0;
    }

    @Override
    public int delete(String query) {
        return 0;
    }

    @Override
    public int create(String query) {
        return 0;
    }

    @Override
    public int select(String query) {
        return 0;
    }


}
