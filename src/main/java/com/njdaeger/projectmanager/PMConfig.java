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

    private String databaseFormat;

    private Configuration config;
    private File configFile;

    private final ProjectManager plugin;

    public PMConfig(ProjectManager plugin) {
        this.plugin = plugin;
        this.config = createConfig();

        this.clientId = getOrSet("bot.clientId", "No clientId specified.", "");
        this.clientSecret = getOrSet("bot.clientSecret", "No clientSecret specified.", "");
        this.botToken = getOrSet("bot.token", "No bot token specified. Visit https://discord.com/developers/applications/ and go to your created applicaton, select 'bot' on the left navigation, and click 'Reset Token', or copy the token if it is available.", "");

        this.port = getOrSet("web.port", "No webserver port specified or port number is too low. Defaulting to 8080.", 8080, port -> port < 1024);
        this.requestLogging = getOrSet("web.requestLogging", "No request logging specified, no requests to the webapp will be logged to console.", false);
        this.sessionExpireTime = getOrSet("web.sessionExpireTime", "No sessionExpireTime specified in the config, defaulting to 1 hour.", 3600000);
        this.otpExpireTime = getOrSet("web.otpExpireTime", "otpExpireTime either too low (less than 10 seconds) or not specified. Defaulting to 5 minutes.", 6000000);

        this.verboseLogging = getOrSet("plugin.verboseLogging",  "No verbose logging specified, defaulting to false.", false);
        this.databaseFormat = getOrSet("plugin.databaseFormat", "No database format specified, defaulting to 'sql' Options are 'sql' and 'yml'", "sql");

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


    public String getDatabaseFormat() {
        return databaseFormat;
    }
}
