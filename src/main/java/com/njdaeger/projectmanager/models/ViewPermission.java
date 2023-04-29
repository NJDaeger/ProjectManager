package com.njdaeger.projectmanager.models;

import org.jetbrains.annotations.NotNull;

public record ViewPermission(int id, @NotNull String permission, @NotNull String niceName) {
}
