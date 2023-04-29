package com.njdaeger.projectmanager.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Status(int id, @NotNull String statusName, @Nullable String statusColor) {
}
