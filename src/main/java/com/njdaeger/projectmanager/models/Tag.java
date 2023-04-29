package com.njdaeger.projectmanager.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Tag(int id, @NotNull String tag, @Nullable String color) {

}
