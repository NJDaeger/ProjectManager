package com.njdaeger.projectmanager;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PMConfig {

    //bot configuration
    private String clientId;
    private String clientSecret;
    private String botToken;


    //webapp configuration
    private int port;
    private boolean requestLogging;
    private long sessionExpireTime;
    private long otpExpireTime;


    private boolean verboseLogging;

    private Configuration config;
    private File configFile;

    private final ProjectManager plugin;

    public PMConfig(ProjectManager plugin) {
        this.plugin = plugin;
        this.config = createConfig();
        this.clientId = config.getString("bot.clientId");
        if (this.clientId == null || this.clientId.isEmpty()) {
            plugin.getLogger().warning("No client id specified.");
            config.set("bot.clientId", "");
            this.clientId = "";
        }
        this.clientSecret = config.getString("bot.clientSecret");
        if (this.clientSecret == null || this.clientSecret.isEmpty()) {
            plugin.getLogger().warning("No client secret specified.");
            config.set("bot.clientSecret", "");
            this.clientSecret = "";
        }
        this.botToken = config.getString("bot.token");
        if (this.botToken == null || this.botToken.isEmpty()) {
            plugin.getLogger().warning("No bot token specified. Visit https://discord.com/developers/applications/ and go to your created application, select 'bot' on the left navigation, and click 'Reset Token', or copy the token if it is available.");
            config.set("bot.token", "");
            this.botToken = "";
        }

        this.port = config.getInt("web.port");
        if (this.port == 0 && config.get("web.port") == null) {
            plugin.getLogger().warning("No webserver port specified. Defaulting to 8080.");
            config.set("web.port", 8080);
            this.port = 8080;
        }

        if (this.port < 1024) throw new RuntimeException("You cannot set a port less than 1024.");

        this.requestLogging = config.getBoolean("web.requestLogging");
        if (config.get("web.requestLogging") == null) {
            plugin.getLogger().warning("No request logging specified, no requests to the webapp will be logged to console.");
            config.set("web.requestLogging", false);
            this.requestLogging = false;
        }

        this.sessionExpireTime = config.getLong("web.sessionExpireTime");
        if (config.get("web.sessionExpireTime") == null) {
            plugin.getLogger().warning("No sessionExpireTime specified in the config, defaulting to 3600000ms (1 hour)");
            config.set("web.sessionExpireTime", 3600000L);
            this.sessionExpireTime = 3600000L;
        }

        this.otpExpireTime = config.getLong("web.otpExpireTime");
        if (this.otpExpireTime < 10000L || config.get("web.sessionExpireTime") == null) {
            plugin.getLogger().warning("otpExpireTime either too low (less than 10 seconds) or not specified. Defaulting to 5 minutes.");
            config.set("web.otpExpireTime", 600000L);
            this.sessionExpireTime = 600000L;
        }

        this.requestLogging = config.getBoolean("plugin.verboseLogging");
        if (config.get("plugin.verboseLogging") == null) {
            plugin.getLogger().warning("No verbose logging specified, defaulting to false");
            config.set("plugin.verboseLogging", false);
            this.requestLogging = false;
        }
        var cfg = (YamlConfiguration) config;
        try {
            cfg.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String getBotToken() {
        return botToken;
    }

    public String getClientId() {
        return clientId;
    }


    //todo remove if no longer needed
    public String getClientSecret() {
        return clientSecret;
    }

    public int getWebappPort() {
        return port;
    }

    public boolean doRequestLogging() {
        return requestLogging;
    }

    public long getSessionExpireTime() {
        return sessionExpireTime;
    }

    public long getOtpExpireTime() {
        return otpExpireTime;
    }

    public boolean doVerboseLogging() {
        return verboseLogging;
    }

    private Configuration createConfig() {
        if (config != null) return config;
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.configFile = new File(plugin.getDataFolder().getAbsoluteFile() + File.separator + "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("The configuration file was unable to be created or loaded.");
                config = null;
            }
        }
        return config = YamlConfiguration.loadConfiguration(configFile);
    }


}
