package com.njdaeger.projectmanager.services;

import com.njdaeger.projectmanager.models.ManagedWorld;

import java.util.List;
import java.util.UUID;

public interface IWorldService {

    /**
     * Get a world by its unique id
     * @param worldId The world id
     * @return The found world, or null if no world was found with that id
     */
    ManagedWorld getWorldById(UUID worldId);

    /**
     * Create a new world
     * @param worldId The unique id of this world
     * @param worldName The name of this world
     * @return The newly created world, or the existing user if a user was found with that userId
     */
    ManagedWorld createWorld(UUID worldId, String worldName);

    /**
     * Delete a world
     * @param worldId The unique id of the world to be deleted
     * @return The deleted world, or null if no world was found to be deleted
     */
    ManagedWorld deleteWorld(UUID worldId);

    /**
     * Update a world name
     * @param worldId The unique id of the world to be updated
     * @param worldName The new worldname of this world
     * @return The updated world, or null if no world was updated
     */
    ManagedWorld updateWorldName(UUID worldId, String worldName);

    /**
     * Update a world id
     * @param oldWorldId The old world id
     * @param newWorldId The new world id
     * @return The updated world, or null if no world was updated
     */
    ManagedWorld updateWorldId(UUID oldWorldId, UUID newWorldId);

    /**
     * Update a world
     * @param oldWorldId The old world id
     * @param newWorldId The new world id
     * @param worldName The new worldname
     * @return The updated world, or null if no world was updated
     */
    ManagedWorld updateWorld(UUID oldWorldId, UUID newWorldId, String worldName);

    /**
     * Get a list of managed worlds currently on the server
     * @return A list of managed worlds, or an empty list if no worlds are currently managed
     */
    List<ManagedWorld> getWorlds();

}
