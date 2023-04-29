package com.njdaeger.projectmanager.dataaccess.sql.impl.services;

import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.dataaccess.sql.impl.SqlDataAccess;
import com.njdaeger.projectmanager.models.User;
import com.njdaeger.projectmanager.services.IUserService;
import com.njdaeger.projectmanager.utils.CacheMap;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

import static com.njdaeger.projectmanager.dataaccess.sql.impl.SqlUtils.sql;

public class UserServiceImpl implements IUserService {

    private final ProjectManager plugin;
    private final SqlDataAccess connection;
    private final Map<UUID, User> userCache;

    public UserServiceImpl(ProjectManager plugin, SqlDataAccess connection) {
        this.userCache = new CacheMap<>(900000);//cache timeout after 15 minutes
        this.connection = connection;
        this.plugin = plugin;

        plugin.verbose("User service initialized.");
    }

    @Override
    public User getUserBy(UUID userId) {
        var hit = userCache.get(userId);
        if (hit != null) return hit;

        try (var stmt = connection.getProvider().prepareStatement(sql("SELECT * FROM _prefix_users WHERE uuid=?"))) {
            stmt.setString(1, userId.toString());
            var res = stmt.executeQuery();

            if (!res.next()) return null;
            var user = new User(res.getInt("id"), userId, res.getString("username"));
            userCache.put(userId, user);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User createUser(UUID userId, String username) {
        var hit = getUserBy(userId);
        if (hit != null) return hit;
        try (var stmt = connection.getProvider().prepareStatement(sql("INSERT INTO _prefix_users (uuid, username) VALUES (?,?)"), Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, userId.toString());
            stmt.setString(2, username);
            stmt.executeUpdate();

            var res = stmt.getGeneratedKeys();
            if (res.next()) {
                var user = new User(res.getInt("GENERATED_KEY"), userId, username);
                userCache.put(userId, user);
                return user;
            } throw new RuntimeException("No key generated for user creation: " + userId + " [" + username + "]");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User deleteUser(UUID userIdToDelete) {
        var hit = getUserBy(userIdToDelete);
        if (hit == null) return null;//they were never in the db to begin with
        try (var stmt = connection.getProvider().prepareStatement(sql("DELETE FROM _prefix_users WHERE uuid=?"))) {
            stmt.setString(1, userIdToDelete.toString());
            stmt.executeUpdate();

            return userCache.remove(userIdToDelete);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User updateUserName(UUID userId, String newUsername) {
        var hit = getUserBy(userId);
        if (hit == null) return null;//they were never in the db to begin with
        try (var stmt = connection.getProvider().prepareStatement(sql("UPDATE _prefix_users SET username=? WHERE uuid=?"))) {
            stmt.setString(1, newUsername);
            stmt.setString(2, userId.toString());
            stmt.executeUpdate();

            var user = new User(hit.id(), userId, newUsername);
            userCache.remove(userId);
            userCache.put(userId, user);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User updateUserId(UUID oldUserId, UUID newUserId) {
        var hit = getUserBy(oldUserId);
        if (hit == null) return null;//they were never in the db to begin with
        var hitNew = getUserBy(newUserId);
        if (hitNew != null) {
            //todo this needs to be written
            //this is where it gets a bit more complicated... since the new user Id we need to shift to already exists as a user, we now need to perform a merge and a deletion.
            //anything referencing the newUser data in the database needs to be updated to point towards the old user data.
            throw new UnsupportedOperationException("Support when user ids already exist.");
        }
        try (var stmt = connection.getProvider().prepareStatement(sql("UPDATE _prefix_users SET uuid=? WHERE uuid=?"))) {
            stmt.setString(1, newUserId.toString());
            stmt.setString(2, oldUserId.toString());
            stmt.executeUpdate();

            var user = new User(hit.id(), newUserId, hit.userName());
            userCache.remove(oldUserId);
            userCache.put(newUserId, user);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User updateUser(UUID oldUserId, UUID newUserId, String username) {
        var hit = getUserBy(oldUserId);
        if (hit == null) return null;//they were never in the db to begin with
        var hitNew = getUserBy(newUserId);
        if (hitNew != null) {
            //todo this needs to be written
            //this is where it gets a bit more complicated... since the new user Id we need to shift to already exists as a user, we now need to perform a merge and a deletion.
            //anything referencing the newUser data in the database needs to be updated to point towards the old user data.
            throw new UnsupportedOperationException("Support when user ids already exist.");
        }
        try (var stmt = connection.getProvider().prepareStatement(sql("UPDATE _prefix_users SET uuid=?, username=? WHERE uuid=?"))) {
            stmt.setString(1, newUserId.toString());
            stmt.setString(2, username);
            stmt.setString(3, oldUserId.toString());
            stmt.executeUpdate();

            var user = new User(hit.id(), newUserId, username);
            userCache.remove(oldUserId);
            userCache.put(newUserId, user);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
