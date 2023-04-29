package com.njdaeger.projectmanager.dataaccess.sql.impl.services;

import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.dataaccess.sql.impl.SqlDataAccess;
import com.njdaeger.projectmanager.models.ManagedWorld;
import com.njdaeger.projectmanager.models.User;
import com.njdaeger.projectmanager.services.IWorldService;
import com.njdaeger.projectmanager.utils.CacheMap;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.njdaeger.projectmanager.dataaccess.sql.impl.SqlUtils.sql;

public class WorldServiceImpl implements IWorldService {

    private final ProjectManager plugin;
    private final SqlDataAccess connection;
    private final Map<UUID, ManagedWorld> worldCache;

    public WorldServiceImpl(ProjectManager plugin, SqlDataAccess connection) {
        this.worldCache = new ConcurrentHashMap<>();
        this.connection = connection;
        this.plugin = plugin;
    }

    @Override
    public ManagedWorld getWorldById(UUID worldId) {
        var hit = worldCache.get(worldId);
        if (hit != null) return hit;

        try (var stmt = connection.getProvider().prepareStatement(sql("SELECT * FROM _prefix_worlds WHERE uuid=?"))) {
            stmt.setString(1, worldId.toString());
            var res = stmt.executeQuery();

            if (!res.next()) return null;
            var world = new ManagedWorld(res.getInt("id"), worldId, res.getString("worldname"));
            worldCache.put(worldId, world);
            return world;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ManagedWorld createWorld(UUID worldId, String worldName) {
        var hit = worldCache.get(worldId);
        if (hit != null) return hit;

        try (var stmt = connection.getProvider().prepareStatement(sql("INSERT INTO _prefix_worlds (uuid, worldname) VALUES (?,?)"), Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, worldId.toString());
            stmt.setString(2, worldName);
            stmt.executeUpdate();

            var res = stmt.getGeneratedKeys();
            if (res.next()) {
                var world = new ManagedWorld(res.getInt("id"), worldId, worldName);
                worldCache.put(worldId, world);
                return world;
            } throw new RuntimeException("No key generated for world creation: " + worldId + " [" + worldName + "]");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ManagedWorld deleteWorld(UUID worldId) {
        var hit = getWorldById(worldId);
        if (hit == null) return null;

        try (var stmt = connection.getProvider().prepareStatement(sql("DELETE FROM _prefix_worlds WHERE uuid=?"), Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, worldId.toString());
            stmt.executeUpdate();

            return worldCache.remove(worldId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ManagedWorld updateWorldName(UUID worldId, String worldName) {
        var hit = getWorldById(worldId);
        if (hit == null) return null;//they were never in the db to begin with
        try (var stmt = connection.getProvider().prepareStatement(sql("UPDATE _prefix_worlds SET worldname=? WHERE uuid=?"))) {
            stmt.setString(1, worldName);
            stmt.setString(2, worldId.toString());
            stmt.executeUpdate();

            var world = new ManagedWorld(hit.id(), worldId, worldName);
            worldCache.remove(worldId);
            worldCache.put(worldId, world);
            return world;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ManagedWorld updateWorldId(UUID oldWorldId, UUID newWorldId) {
        var hit = getWorldById(oldWorldId);
        if (hit == null) return null;//they were never in the db to begin with
        var hitNew = getWorldById(newWorldId);
        if (hitNew != null) {
            //todo this needs to be written
            //this is where it gets a bit more complicated... any references to the new world that existed before needs to be shifted to the old world.
            throw new UnsupportedOperationException("Support when world ids already exist.");
        }
        try (var stmt = connection.getProvider().prepareStatement(sql("UPDATE _prefix_worlds SET uuid=? WHERE uuid=?"))) {
            stmt.setString(1, newWorldId.toString());
            stmt.setString(2, oldWorldId.toString());
            stmt.executeUpdate();

            var world = new ManagedWorld(hit.id(), newWorldId, hit.worldName());
            worldCache.remove(oldWorldId);
            worldCache.put(newWorldId, world);
            return world;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ManagedWorld updateWorld(UUID oldWorldId, UUID newWorldId, String worldName) {
        var hit = getWorldById(oldWorldId);
        if (hit == null) return null;//they were never in the db to begin with
        var hitNew = getWorldById(newWorldId);
        if (hitNew != null) {
            //todo this needs to be written
            //this is where it gets a bit more complicated... any references to the new world that existed before needs to be shifted to the old world.
            throw new UnsupportedOperationException("Support when world ids already exist.");
        }
        try (var stmt = connection.getProvider().prepareStatement(sql("UPDATE _prefix_worlds SET uuid=?, worldname=? WHERE uuid=?"))) {
            stmt.setString(1, newWorldId.toString());
            stmt.setString(2, worldName);
            stmt.setString(3, oldWorldId.toString());
            stmt.executeUpdate();

            var world = new ManagedWorld(hit.id(), newWorldId, worldName);
            worldCache.remove(oldWorldId);
            worldCache.put(newWorldId, world);
            return world;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ManagedWorld> getWorlds() {
        if (worldCache.isEmpty()) {
            try (var stmt = connection.getProvider().prepareStatement(sql("SELECT * FROM _prefix_worlds"))) {
                var res = stmt.executeQuery();

                var worlds = new ArrayList<ManagedWorld>();
                if (!res.next()) return worlds;

                do {
                    var world = new ManagedWorld(res.getInt("id"), UUID.fromString(res.getString("uuid")), res.getString("worldname"));
                    worlds.add(world);
                    worldCache.put(world.worldId(), world);
                } while (res.next());


                return worlds;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else return worldCache.values().stream().toList();
    }
}
