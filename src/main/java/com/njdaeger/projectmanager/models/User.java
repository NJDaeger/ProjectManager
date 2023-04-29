package com.njdaeger.projectmanager.models;

import java.util.UUID;

public record User(int id, UUID minecraftId, String userName) {
}
