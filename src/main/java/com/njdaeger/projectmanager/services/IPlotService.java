package com.njdaeger.projectmanager.services;

import com.njdaeger.projectmanager.webapp.model.Plot;
import com.njdaeger.projectmanager.webapp.model.PlotType;
import com.njdaeger.projectmanager.webapp.model.Tag;
import org.bukkit.Location;

import java.util.List;
import java.util.function.Predicate;

public interface IPlotService {

    Plot getPlotById(int plotId);

    Plot createPlot(Plot parent, String name, String description, Tag[] tags, PlotType plotType, Location location, String[] requiredPermissionToView, String[] recommendedPermission);

    List<Plot> findPlotByTag(Tag tag);

    List<Plot> findPlotByName(String name);

    List<Plot> findPlot(Predicate<Plot> plotFilter);

    boolean updatePlotParent(Plot currentPlot, Plot newParent);

    boolean updatePlotName(Plot currentPlot, String plotName);

    boolean updatePlotDescription(Plot currentPlot, String description);

    boolean updatePlotTags(Plot currentPlot, Tag[] tags);

    boolean updatePlotType(Plot currentPlot, PlotType type);

    boolean updatePlotLocation(Plot currentPlot, Location location);

    boolean updatePermissionToView(Plot currentPlot, String[] requiredPermissionsToView);

    boolean updateRecommendedPermission(Plot currentPlot, String[] recommendedPermission);


}
