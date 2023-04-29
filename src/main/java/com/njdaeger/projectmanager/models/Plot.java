package com.njdaeger.projectmanager.models;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a plot
 * @param id The unique ID of this plot
 * @param parent The parent plot, if it has a parent. Cannot reference itself. Nullable
 * @param name The name of this plot. Not null
 * @param description The description of this plot. Nullable
 * @param tags Tags associated with this plot
 * @param type Type of plot
 * @param group The group this plot belongs to
 * @param location The location of this plot
 * @param status The status of this plot
 * @param users The users building this plot
 * @param requiredPermissionToView The list of permissions that a user must have one of in order to view this plot.
 * @param recommendedPermission The list of permissions that a user should have one of to be able to take this plot without staff approval.
 */
public record Plot(int id,
                   @Nullable Plot parent,
                   @NotNull String name,
                   @Nullable String description,
                   @Nullable Tag[] tags,
                   @Nullable Type type,
                   @Nullable Group group,
                   @NotNull Location location,
                   @NotNull Status status,
                   @Nullable User[] users,
                   @Nullable ViewPermission[] requiredPermissionToView,
                   @Nullable RecommendedPermission[] recommendedPermission) {
}
