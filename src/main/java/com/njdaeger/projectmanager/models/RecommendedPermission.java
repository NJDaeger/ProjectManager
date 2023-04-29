package com.njdaeger.projectmanager.models;

import org.jetbrains.annotations.NotNull;

public record RecommendedPermission(int id, @NotNull String permission, String niceName) {
}
