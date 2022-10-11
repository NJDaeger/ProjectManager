package com.njdaeger.projectmanager;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

public class PMConfig {

    //database configuration
    private String databaseHost;
    private int databasePort;
    private String databaseName;
    private String databaseUsername;
    private String databasePassword;
    private String databasePrefix;

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

        this.databasePort = getOrSet("database.port", "The database port must be greater than or equal to 1024. Defaulting to " + 3306, 3306, (val) -> val < 1024);
        this.databaseHost = getOrSet("database.host", "The database host must be set. Defaulting to '127.0.0.1'", "127.0.0.1", String::isEmpty);
        this.databaseName = getOrSet("database.name", "The database name must be set. Defaulting to 'projectmanager'", "projectmanager", String::isEmpty);
        this.databaseUsername = getOrSet("database.username", "The database username must be set. Defaulting to 'root'", "root", String::isEmpty);
        this.databasePassword = getOrSet("database.password", "The database password must be set. Defaulting to 'password'", "password", String::isEmpty);
        this.databasePrefix = getOrSet("database.prefix", "No database prefix defined. Defaulting to an empty string.", "");

        var cfg = (YamlConfiguration) config;
        try {
            cfg.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Get or set a configuration variable
     * @param path The configuration path
     * @param warning The warning spat into console when the value is not set
     * @param defaultValue The default value to be set/returned if the value matches the "shouldNotMatch" predicate.
     * @param shouldNotMatch The test this config value should return false for. If this predicate returns true on the config value, the default value will be used.
     * @param <T> The type of value being set
     * @return The set or gotten configuration value.
     */
    private <T> T getOrSet(String path, String warning, T defaultValue, Predicate<T> shouldNotMatch) {
        if (shouldNotMatch != null && shouldNotMatch.test(defaultValue)) throw new RuntimeException("Default value for setting '" + path + "' cannot fail the restraint check.");
        if (config.get(path) == null || (shouldNotMatch != null && shouldNotMatch.test((T)config.get(path)))) {
            if (warning != null) plugin.getLogger().warning(warning);
            config.set(path, defaultValue);
            return defaultValue;
        } else return (T) config.get(path);
    }

    /**
     * Get or set a configuration variable
     * @param path The configuration path
     * @param warning The warning spat into console when the value is not set.
     * @param defaultValue The default value to be set/returned if the value is not set.
     * @param <T> The type of value being set
     * @return The set or gotten configuration value.
     */
    private <T> T getOrSet(String path, String warning, T defaultValue) {
        return getOrSet(path, warning, defaultValue, null);
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public int getDatabasePort() {
        return databasePort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public String getDatabasePrefix() {
        return databasePrefix;
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
