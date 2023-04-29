package com.njdaeger.projectmanager.dataaccess.sql.impl.services;

import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.dataaccess.sql.impl.SqlDataAccess;
import com.njdaeger.projectmanager.models.Tag;
import com.njdaeger.projectmanager.services.IConfigService;
import com.njdaeger.projectmanager.services.Result;
import com.njdaeger.projectmanager.utils.CacheMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.njdaeger.projectmanager.dataaccess.sql.impl.SqlUtils.sql;

public class ConfigServiceImpl implements IConfigService {

    private final SqlDataAccess connection;
    private final ProjectManager plugin;
    private final Map<Integer, Tag> tagCache;

    public ConfigServiceImpl(ProjectManager plugin, SqlDataAccess connection) {
        this.plugin = plugin;
        this.connection = connection;
        this.tagCache = new HashMap<>();


        //preload tags into cache, since we likely dont have enough to have too large of a performance hit from it
        try (var stmt = connection.getProvider().prepareStatement(sql("SELECT * FROM _prefix_tags"))) {
            var res = stmt.executeQuery();

            while (res.next()) {
                var tag = new Tag(res.getInt("id"), res.getString("name"), res.getString("color"));
                tagCache.put(tag.id(), tag);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        plugin.verbose("Config service initialized.");
    }

    @Override
    public Tag getTagById(int id) {
        var hit = tagCache.get(id);
        if (hit != null) return hit;

        try (var stmt = connection.getProvider().prepareStatement(sql("SELECT * FROM _prefix_tags where id=?"))) {
            stmt.setInt(1, id);
            var res = stmt.executeQuery();

            if (!res.next()) return null;
            var tag = new Tag(res.getInt("id"), res.getString("name"), res.getString("color"));
            tagCache.put(id, tag);
            return tag;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<Tag> createTag(@NotNull String tagName, @Nullable String tagColor) {
        var hit = getTagByName(tagName);
        if (hit != null) return Result.bad(hit, "A tag with that name already exists. Please choose another name.");
        try (var stmt = connection.getProvider().prepareStatement(sql("INSERT INTO _prefix_tags (name, color) values (?, ?)"), Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tagName);
            if (tagColor == null || tagColor.isBlank()) stmt.setNull(2, Types.VARCHAR);
            else stmt.setString(2, tagColor);
            stmt.executeUpdate();

            var res = stmt.getGeneratedKeys();
            if (res.next()) {
                var tag = new Tag(res.getInt("GENERATED_KEY"), tagName, tagColor);
                tagCache.put(tag.id(), tag);
                return Result.good(tag);
            } throw new RuntimeException("No key generated for tag creation: " + tagName + " [" + tagColor + "]");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Tag getTagByName(@NotNull String tagName) {
        var hit = tagCache.values().stream().filter(tag -> tag.tag().equalsIgnoreCase(tagName)).findFirst().orElse(null);
        if (hit != null) return hit;
        try (var stmt = connection.getProvider().prepareStatement(sql("SELECT * FROM _prefix_tags WHERE name=?"))) {
            stmt.setString(1, tagName);
            var res = stmt.executeQuery();

            if (!res.next()) return null;
            var tag = new Tag(res.getInt("id"), res.getString("name"), res.getString("color"));
            tagCache.put(tag.id(), tag);
            return tag;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<Tag> updateTagName(@NotNull Tag tag, @NotNull String tagName) {
//        var hit = getTagById(tag.id());
//        if (hit == null) return Result.bad(null, "A tag with the ID of " + tag.id() + " does not exists.");
        var hit = getTagByName(tagName);
        if (hit != null) return Result.bad(hit, "A tag with that name already exists. Please choose another name.");

        try (var stmt = connection.getProvider().prepareStatement(sql("UPDATE _prefix_tags SET name=? WHERE id=?"))) {
            stmt.setString(1, tagName);
            stmt.setInt(2, tag.id());
            stmt.executeUpdate();

            var newTag = new Tag(tag.id(), tagName, tag.color());
            tagCache.put(tag.id(), newTag);

            return Result.good(newTag);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<Tag> updateTagColor(@NotNull Tag tag, @Nullable String color) {
//        var hit = getTagById(tag.id());
//        if (hit == null) return Result.bad(null, "A tag with the ID of " + tag.id() + " does not exists.");
        try (var stmt = connection.getProvider().prepareStatement(sql("UPDATE _prefix_tags SET color=? WHERE id=?"))) {
            stmt.setString(1, color);
            stmt.setInt(2, tag.id());
            stmt.executeUpdate();

            var newTag = new Tag(tag.id(), tag.tag(), color);
            tagCache.put(tag.id(), newTag);

            return Result.good(newTag);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Tag> getTags() {
        return tagCache.values().stream().toList();
    }
}
