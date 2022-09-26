package com.njdaeger.projectmanager.webapp.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Tag(int id, @NotNull String tag, @Nullable String color) {
}
