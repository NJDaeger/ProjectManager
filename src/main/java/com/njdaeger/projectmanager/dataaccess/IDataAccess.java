package com.njdaeger.projectmanager.dataaccess;

public interface IDataAccess {

    void initializeDatabase();

    int update(String query);

    int delete(String query);

    int create(String query);

    int select(String query);

}
