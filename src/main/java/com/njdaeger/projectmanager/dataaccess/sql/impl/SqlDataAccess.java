package com.njdaeger.projectmanager.dataaccess.sql.impl;

import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.dataaccess.IDataAccess;
import com.njdaeger.projectmanager.dataaccess.IDatabaseInitializer;
import com.njdaeger.projectmanager.dataaccess.sql.impl.services.ConfigServiceImpl;
import com.njdaeger.projectmanager.dataaccess.sql.impl.services.PlotServiceImpl;
import com.njdaeger.projectmanager.dataaccess.sql.impl.services.UserServiceImpl;
import com.njdaeger.projectmanager.dataaccess.sql.impl.services.WorldServiceImpl;
import com.njdaeger.projectmanager.services.IConfigService;
import com.njdaeger.projectmanager.services.IPlotService;
import com.njdaeger.projectmanager.services.IUserService;
import com.njdaeger.projectmanager.services.IWorldService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SqlDataAccess implements IDataAccess<Connection> {

    private Connection database;
    private final String prefix;
    private final ProjectManager plugin;

    private IPlotService plotService;
    private IUserService userService;
    private IWorldService worldService;
    private IConfigService configService;

    private static final Map<Integer, IDatabaseInitializer> databaseVersions = new HashMap<>(){{
        put(0, new SqlDatabaseV0());
    }};

    public SqlDataAccess(ProjectManager plugin) {
        this.prefix = plugin.getPMConfig().getDatabasePrefix();
        this.plugin = plugin;
        initializeDatabase();

        this.plotService = new PlotServiceImpl(plugin, this);
        this.userService = new UserServiceImpl(plugin, this);
        this.worldService = new WorldServiceImpl(plugin, this);
        this.configService = new ConfigServiceImpl(plugin, this);
    }

    public Connection getProvider() {
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
    public int getLatestDatabaseVersion() {
        return 0;
    }

    @Override
    public void initializeDatabase() {
        try {
            int version = -1;
            var data = getProvider().getMetaData();
            var tables = data.getTables(null, null, prefix + "version", null);
            if (tables.next()) {
                try (var statement = getProvider().createStatement()) {
                    var res = statement.executeQuery("SELECT * FROM " + prefix + "version");
                    if (res.next()) version = res.getInt("databaseVersion");
                    plugin.verbose("[SqlDataAccess] Database version " + version);
                }
            }

            //if the version is -1 we want to initialize a new database
            if (version == -1) {
                plugin.verbose("[SqlDataAccess] Initializing new database install...");
                databaseVersions.get(getLatestDatabaseVersion()).initializeNewDatabase(getProvider());
            } //todo add support for running database version upgrades
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IPlotService getPlotService() {
        return null;
    }

    @Override
    public IUserService getUserService() {
        return userService;
    }

    @Override
    public IWorldService getWorldService() {
        return worldService;
    }

    @Override
    public IConfigService getConfigService() {
        return configService;
    }


}
