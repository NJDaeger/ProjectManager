package com.njdaeger.projectmanager.services;

import com.njdaeger.projectmanager.webapp.model.User;

import java.util.UUID;

public interface IUserService {

    /**
     * Get a user by their minecraft UUID
     * @param userId Their minecraft uuid
     * @return The found user, or null if no user was found with that mapping.
     */
    User getUserBy(UUID userId);

    /**
     * Create a new user
     * @param userId Their minecraft uuid
     * @return The newly created user, or null if a user was found with that userId
     */
    User createUser(UUID userId);

    /**
     * Delete a user
     *
     * This will remove all references towards this user in the database
     *
     * @param userIdToDelete The user to delete
     * @return True if the user was successfully deleted from the database, false otherwise.
     */
    boolean deleteUser(UUID userIdToDelete);

    /**
     * Update a user
     *
     * @param oldUserId Their old minecraft UUID
     * @param newUserId Their new minecraft UUID
     * @return True if the user was updated, false otherwise
     */
    boolean updateUser(UUID oldUserId, UUID newUserId);

}
