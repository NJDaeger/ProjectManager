package com.njdaeger.projectmanager.dataaccess.sql.impl;

import com.njdaeger.projectmanager.ProjectManager;
import org.jetbrains.annotations.NotNull;

public class SqlUtils {


    /**
     * Parse a sql query to replace all instances of the word "prefix" with the prefix defined in the PM config file.
     * @param sql The sql query
     * @return The Sql query with the prefix inserted
     */
    public static String sql(@NotNull String sql) {
        var plugin = ProjectManager.getPlugin(ProjectManager.class);
        var prefix = plugin.getPMConfig().getDatabasePrefix();
        var statement = sql.replaceAll("_prefix_", prefix);
        plugin.verbose("Statement: " + statement);
        return statement;
    }

}
