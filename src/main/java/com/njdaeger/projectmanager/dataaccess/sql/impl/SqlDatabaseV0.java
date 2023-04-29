package com.njdaeger.projectmanager.dataaccess.sql.impl;

import com.njdaeger.projectmanager.dataaccess.IDatabaseInitializer;

import java.sql.Connection;
import java.sql.SQLException;

import static com.njdaeger.projectmanager.dataaccess.sql.impl.SqlUtils.sql;

public class SqlDatabaseV0 implements IDatabaseInitializer {

    @Override
    public int getDatabaseFormatVersion() {
        return 0;
    }

    //TODO add date created and date modified columns and populate them with triggers
    @Override
    public void initializeNewDatabase(Connection connection) throws SQLException {
        var statement = connection.createStatement();
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_version
                (
                    databaseVersion int NOT NULL
                )"""));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_users
                (
                    id int NOT NULL AUTO_INCREMENT,
                    uuid char(36) NOT NULL,
                    username varchar(16) NOT NULL,
                    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    modified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT PRIMARY KEY (id),
                    CONSTRAINT UC_uuid_forUsers UNIQUE (uuid)
                )"""));
        statement.addBatch(sql("""
                CREATE TRIGGER _prefix_usersModifiedDate
                AFTER UPDATE ON _prefix_users
                FOR EACH ROW
                BEGIN
                    UPDATE _prefix_users SET modified = CURRENT_TIMESTAMP WHERE id = NEW.id;
                END
                """));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_tags
                (
                    id int NOT NULL AUTO_INCREMENT,
                    name varchar(64) NOT NULL,
                    color char(6),
                    CONSTRAINT PRIMARY KEY (id),
                    CONSTRAINT UC_name_forTags UNIQUE (name)
                )"""));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_status
                (
                    id int NOT NULL AUTO_INCREMENT,
                    name varchar(64) NOT NULL,
                    color char(6),
                    CONSTRAINT PRIMARY KEY (id),
                    CONSTRAINT UC_name_forStatus UNIQUE (name)
                )"""));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_plotTypes
                (
                    id int NOT NULL AUTO_INCREMENT,
                    name varchar(64) NOT NULL,
                    CONSTRAINT PRIMARY KEY (id),
                    CONSTRAINT UC_name_forTypes UNIQUE (name)
                )"""));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_worlds
                (
                    id int NOT NULL AUTO_INCREMENT,
                    uuid char(36) NOT NULL,
                    worldname varchar(64),
                    CONSTRAINT PRIMARY KEY (id),
                    CONSTRAINT UC_uuid_forWorlds UNIQUE (uuid)
                )"""));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_viewPermissions
                (
                    id int NOT NULL AUTO_INCREMENT,
                    permission varchar(256) NOT NULL,
                    niceName varchar(64) NOT NULL,
                    CONSTRAINT PRIMARY KEY (id),
                    CONSTRAINT UC_permission_forViewPerms UNIQUE (permission),
                    CONSTRAINT UC_name_forViewPerms UNIQUE (niceName)
                )"""));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_recommendedPermissions
                (
                    id int NOT NULL AUTO_INCREMENT,
                    permission varchar(256) NOT NULL,
                    niceName varchar(64),
                    CONSTRAINT PRIMARY KEY (id),
                    CONSTRAINT UC_permission_forRecPerms UNIQUE (permission),
                    CONSTRAINT UC_name_forRecPerms UNIQUE (niceName)
                )"""));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_plots
                (
                    id int NOT NULL AUTO_INCREMENT,
                    name varchar(512) NOT NULL,
                    worldId int NOT NULL,
                    dimensionId int NOT NULL,
                    locX int NOT NULL,
                    locY int NOT NULL,
                    locZ int NOT NULL,
                    statusId int NOT NULL,
                    parentId int,
                    description varchar(4096),
                    plotTypeId int,
                    CONSTRAINT PRIMARY KEY (id),
                    CONSTRAINT FK_parentId FOREIGN KEY (parentId) REFERENCES _prefix_plots(id),
                    CONSTRAINT FK_plotTypeId FOREIGN KEY (plotTypeId) REFERENCES _prefix_plotTypes(id),
                    CONSTRAINT FK_worldId FOREIGN KEY (worldId) REFERENCES _prefix_worlds(id),
                    CONSTRAINT FK_statusId FOREIGN KEY (statusId) REFERENCES _prefix_status(id)
                )"""));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_plotTags
                (
                    plotId int NOT NULL,
                    tagId int NOT NULL,
                    CONSTRAINT FK_plotId_forTags FOREIGN KEY (plotId) REFERENCES _prefix_plots(id),
                    CONSTRAINT FK_tagId FOREIGN KEY (tagId) REFERENCES _prefix_tags(id),
                    CONSTRAINT UC_plot_tag_forPlotTags UNIQUE (plotId, tagId)
                )"""));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_plotUsers
                (
                    plotId int NOT NULL,
                    userId int NOT NULL,
                    CONSTRAINT FK_plotId_forUsers FOREIGN KEY (plotId) REFERENCES _prefix_plots(id),
                    CONSTRAINT FK_userId FOREIGN KEY (userId) REFERENCES _prefix_users(id),
                    CONSTRAINT UC_plot_user_forPlotUsers UNIQUE (plotId, userId)
                )"""));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_plotViewPermissions
                (
                    plotId int NOT NULL,
                    viewPermissionId int NOT NULL,
                    CONSTRAINT FK_plotId_forViewPerms FOREIGN KEY (plotId) REFERENCES _prefix_plots(id),
                    CONSTRAINT FK_viewPermissionId FOREIGN KEY (viewPermissionId) REFERENCES _prefix_viewPermissions(id),
                    CONSTRAINT UC_plot_viewPerm_forPlotViewPerms UNIQUE (plotId, viewPermissionId)
                )"""));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_plotRecommendedPermissions
                (
                    plotId int NOT NULL,
                    recommendedPermId int NOT NULL,
                    CONSTRAINT FK_plotId_forRecommendedPerms FOREIGN KEY (plotId) REFERENCES _prefix_plots(id),
                    CONSTRAINT FK_recommendedPermId FOREIGN KEY (recommendedPermId) REFERENCES _prefix_recommendedPermissions(id),
                    CONSTRAINT UC_plot_recPerm_forPlotRecPerms UNIQUE (plotId, recommendedPermId)
                )"""));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_groups 
                (
                    id int NOT NULL AUTO_INCREMENT,
                    name varchar(64) NOT NULL,
                    CONSTRAINT PRIMARY KEY (id),
                    CONSTRAINT UC_name_forGroups UNIQUE (name)
                )
                """));
        statement.addBatch(sql("""
                CREATE TABLE IF NOT EXISTS _prefix_plotGroups
                (
                    groupId int NOT NULL,
                    plotId int NOT NULL,
                    CONSTRAINT FK_groupId FOREIGN KEY (groupId) REFERENCES _prefix_groups(id),
                    CONSTRAINT FK_plotId_forPlotGroups FOREIGN KEY (plotId) REFERENCES _prefix_plots(id),
                    CONSTRAINT UC_plot_forPlotGroups UNIQUE (plotId)
                )
                """));
        statement.executeBatch();
    }

    @Override
    public void updateExistingDatabase(Connection connection) throws SQLException {
    }
}
