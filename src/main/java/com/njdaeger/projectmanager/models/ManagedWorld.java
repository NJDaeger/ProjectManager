package com.njdaeger.projectmanager.models;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ManagedWorld(int id, @NotNull UUID worldId, @NotNull String worldName) {
}
