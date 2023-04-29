package com.njdaeger.projectmanager.dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDatabaseInitializer {

    int getDatabaseFormatVersion();

    void initializeNewDatabase(Connection connection) throws SQLException;

    void updateExistingDatabase(Connection connection) throws SQLException;

}
