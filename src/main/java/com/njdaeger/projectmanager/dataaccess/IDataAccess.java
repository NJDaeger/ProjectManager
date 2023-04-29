package com.njdaeger.projectmanager.dataaccess;

import com.njdaeger.projectmanager.services.IConfigService;
import com.njdaeger.projectmanager.services.IPlotService;
import com.njdaeger.projectmanager.services.IUserService;
import com.njdaeger.projectmanager.services.IWorldService;

public interface IDataAccess<T> {

    int getLatestDatabaseVersion();

    void initializeDatabase();

    IPlotService getPlotService();

    IUserService getUserService();

    IWorldService getWorldService();

    IConfigService getConfigService();

    T getProvider();

//    int update(String query);
//
//    int delete(String query);
//
//    int create(String query);
//
//    int select(String query);

}
