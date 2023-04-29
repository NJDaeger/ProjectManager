package com.njdaeger.projectmanager.services;

import com.njdaeger.projectmanager.models.User;

import java.util.UUID;

public interface IUserService {

    /**
     * Get a user by their minecraft UUID
     *
     * @param userId Their minecraft uuid
     * @return The found user, or null if no user was found with that mapping.
     */
    User getUserBy(UUID userId);

    /**
     * Create a new user
     *
     * @param userId Their minecraft uuid
     * @param username Their minecraft username
     * @return The newly created user, or the existing user if a user was found with that userId
     */
    User createUser(UUID userId, String username);

    /**
     * Delete a user
     * <p>
     * This will remove all references towards this user in the database
     *
     * @param userIdToDelete The user to delete
     * @return The deleted user, or null if no user was found to be deleted.
     */
    User deleteUser(UUID userIdToDelete);

    /**
     * Update a user's username
     *
     * @param userId The users uuid
     * @param newUsername The user's new username
     * @return The updated user, or null if no user was updated
     */
    User updateUserName(UUID userId, String newUsername);

    /**
     * Update a user's uuid. If the new userId already exists as a user, this effectively becomes a merge of users,
     * where the current entry for the old user will have its UUID updated, and the new user will have its user entry
     * deleted and have all keys referencing the ID of the new user pointing towards the now old user
     *
     * @param oldUserId The users old uuid
     * @param newUserId The users new uuid
     * @return The updated user, or null if no user was updated.
     */
    User updateUserId(UUID oldUserId, UUID newUserId);

    /**
     * Update a user
     *
     * @param oldUserId Their old minecraft UUID
     * @param newUserId Their new minecraft UUID
     * @param username Their new minecraft username
     * @return The updated user, or null if no user was updated.
     */
    User updateUser(UUID oldUserId, UUID newUserId, String username);

}
