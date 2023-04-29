package com.njdaeger.projectmanager.webapp.model.web;

import java.util.List;

public record WorldListResponse(List<ManagedWorldResponse> worlds) {

    public record ManagedWorldResponse(String worldId, String worldName) {}

}
