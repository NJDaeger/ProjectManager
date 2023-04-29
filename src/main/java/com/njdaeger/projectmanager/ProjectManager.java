package com.njdaeger.projectmanager;

import com.njdaeger.authenticationhub.ApplicationRegistry;
import com.njdaeger.authenticationhub.discord.DiscordApplication;
import com.njdaeger.authenticationhub.discord.DiscordUserProfile;
import com.njdaeger.projectmanager.dataaccess.IDataAccess;
import com.njdaeger.projectmanager.dataaccess.sql.impl.SqlDataAccess;
import com.njdaeger.projectmanager.discordbot.ProjectManagerBotTask;
import com.njdaeger.projectmanager.plugin.PlotCommands;
import com.njdaeger.projectmanager.webapp.ProjectManagerWebappTask;
import com.njdaeger.projectmanager.webapp.WebappInterface;
import discord4j.common.util.Snowflake;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class ProjectManager extends JavaPlugin {

    private ProjectManagerBotTask projectManagerBotTask;
    private ProjectManagerWebappTask projectManagerWebappTask;
    private DiscordApplication discordApplication;
    private IDataAccess dataAccess;
    private PMConfig config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.config = new PMConfig(this);
//        resolveAuthhubIntegration();


//        startDiscordBot();
        startDatabase();
//        startWebserver();

        new PlotCommands(this);
    }

    @Override
    public void onDisable() {
        this.projectManagerBotTask.onDisable();
        this.projectManagerWebappTask.onDisable();
    }

    public DiscordUserProfile getDiscordUser(UUID uuid) {
        if (discordApplication == null) throw new RuntimeException("Authhub integration has not yet been initialized.");
        var connection = discordApplication.getConnection(uuid);
        if (connection == null) return null;
        return connection.getDiscordProfile();
    }

    public UUID getUserFromSnowflake(Snowflake snowflake) {
        if (discordApplication == null) throw new RuntimeException("Authhub integration has not yet been initialized.");
        var connection = discordApplication.getConnections(user -> user.getDiscordProfile().snowflake().equalsIgnoreCase(snowflake.asString()));
        var res = connection.keySet().stream().findFirst();
        return res.orElse(null);
    }

    public IDataAccess<?> getDataAccess() {
        if (dataAccess == null) throw new RuntimeException("Database has not yet been initialized.");
        return dataAccess;
    }

    public void verbose(String message) {
        if (config.doVerboseLogging()) getLogger().info("[VERBOSE] " + message);
    }

    public PMConfig getPMConfig() {
        return config;
    }

    public WebappInterface getWebappInterface() {
        if (projectManagerWebappTask == null) throw new RuntimeException("The webapp has not yet been initialized.");
        return projectManagerWebappTask.getAppInterface();
    }

    private void startDatabase() {
        getLogger().info("Initializing database...");
        this.dataAccess = switch (config.getDatabaseFormat().toLowerCase()) {
            case "sql" -> new SqlDataAccess(this);
            case "yml" -> throw new UnsupportedOperationException("yml format not supported yet.");
            default -> throw new UnsupportedOperationException("Unknown database format, try 'sql' or 'yml'");
        };
    }


    private void startDiscordBot() {
        getLogger().info("Starting Discord bot...");
        this.projectManagerBotTask = new ProjectManagerBotTask(this, config);
        this.projectManagerBotTask.onEnable();
    }

    private void startWebserver() {
        getLogger().info("Starting PlotMan webserver...");
        this.projectManagerWebappTask = new ProjectManagerWebappTask(this, config);
        this.projectManagerWebappTask.onEnable();
    }

    private void resolveAuthhubIntegration() {
        getLogger().info("Connecting to AuthenticationHub...");
        var service = Bukkit.getServicesManager().getRegistration(ApplicationRegistry.class);
        if (service == null) throw new RuntimeException("AuthenticationHub's ApplicationRegistry service is not available, unable to start.");
        var registry = service.getProvider();
        var application = registry.getApplication(DiscordApplication.class);
        if (application == null) throw new RuntimeException("AuthenticationHub's Discord application is not available, unable to start.");
        else this.discordApplication = application;
    }

}
