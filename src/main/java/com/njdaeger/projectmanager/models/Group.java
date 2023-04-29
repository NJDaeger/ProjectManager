package com.njdaeger.projectmanager.models;

import org.jetbrains.annotations.Nullable;

/**
 * A plot group
 * @param id Plot group Id
 * @param name The name of the plot group (or null if no name specified)
 */
public record Group(int id, @Nullable String name) {
}
