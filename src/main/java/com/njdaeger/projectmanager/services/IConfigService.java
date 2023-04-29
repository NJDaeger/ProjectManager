package com.njdaeger.projectmanager.services;

import com.njdaeger.projectmanager.models.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IConfigService {

    /**
     * Get a tag by its numeric ID
     * @param id Tag's numeric ID
     * @return The found tag, or null if no tag was found with that ID.
     */
    Tag getTagById(int id);

    Result<Tag> createTag(@NotNull String tagName, @Nullable String tagColor);

    Tag getTagByName(@NotNull String tagName);

    Result<Tag> updateTagName(@NotNull Tag tag, @NotNull String tagName);

    Result<Tag> updateTagColor(@NotNull Tag tag, @Nullable String color);

    List<Tag> getTags();

}
