package com.njdaeger.projectmanager.dataaccess.sql.impl.services;

import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.dataaccess.sql.impl.SqlDataAccess;
import com.njdaeger.projectmanager.models.Plot;
import com.njdaeger.projectmanager.models.Type;
import com.njdaeger.projectmanager.models.Tag;
import com.njdaeger.projectmanager.services.IPlotService;
import org.bukkit.Location;

import java.util.List;
import java.util.function.Predicate;

public class PlotServiceImpl implements IPlotService {

    public PlotServiceImpl(ProjectManager plugin, SqlDataAccess connection) {

    }

    @Override
    public Plot getPlotById(int plotId) {
        return null;
    }

    @Override
    public Plot createPlot(Plot parent, String name, String description, Tag[] tags, Type type, Location location, String[] requiredPermissionToView, String[] recommendedPermission) {
        return null;
    }

    @Override
    public List<Plot> findPlotByTag(Tag tag) {
        return null;
    }

    @Override
    public List<Plot> findPlotByName(String name) {
        return null;
    }

    @Override
    public List<Plot> findPlot(Predicate<Plot> plotFilter) {
        return null;
    }

    @Override
    public boolean updatePlotParent(Plot currentPlot, Plot newParent) {
        return false;
    }

    @Override
    public boolean updatePlotName(Plot currentPlot, String plotName) {
        return false;
    }

    @Override
    public boolean updatePlotDescription(Plot currentPlot, String description) {
        return false;
    }

    @Override
    public boolean updatePlotTags(Plot currentPlot, Tag[] tags) {
        return false;
    }

    @Override
    public boolean updatePlotType(Plot currentPlot, Type type) {
        return false;
    }

    @Override
    public boolean updatePlotLocation(Plot currentPlot, Location location) {
        return false;
    }

    @Override
    public boolean updatePermissionToView(Plot currentPlot, String[] requiredPermissionsToView) {
        return false;
    }

    @Override
    public boolean updateRecommendedPermission(Plot currentPlot, String[] recommendedPermission) {
        return false;
    }
}
