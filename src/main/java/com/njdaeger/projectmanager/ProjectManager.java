package com.njdaeger.projectmanager;

import com.njdaeger.authenticationhub.ApplicationRegistry;
import com.njdaeger.authenticationhub.discord.DiscordApplication;
import com.njdaeger.authenticationhub.discord.DiscordUserProfile;
import com.njdaeger.projectmanager.discordbot.ProjectManagerBotTask;
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
    private PMConfig config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.config = new PMConfig(this);
        resolveAuthhubIntegration();

        this.projectManagerBotTask = new ProjectManagerBotTask(this, config);
        this.projectManagerBotTask.onEnable();

        this.projectManagerWebappTask = new ProjectManagerWebappTask(this, config);
        this.projectManagerWebappTask.onEnable();

    }

    @Override
    public void onDisable() {
        this.projectManagerBotTask.onDisable();
        this.projectManagerWebappTask.onDisable();
    }

    public DiscordUserProfile getDiscordUser(UUID uuid) {
        var connection = discordApplication.getConnection(uuid);
        if (connection == null) return null;
        return connection.getDiscordProfile();
    }

    public UUID getUserFromSnowflake(Snowflake snowflake) {
        var connection = discordApplication.getConnections(user -> user.getDiscordProfile().snowflake().equalsIgnoreCase(snowflake.asString()));
        var res = connection.keySet().stream().findFirst();
        return res.orElse(null);
    }

    public void log(String message) {
        if (config.doVerboseLogging()) getLogger().info("[VERBOSE] " + message);
    }

    public PMConfig getPMConfig() {
        return config;
    }

    public WebappInterface getWebappInterface() {
        return projectManagerWebappTask.getAppInterface();
    }

    private void startWebserver() {
        getLogger().info("Starting internal webserver...");
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
