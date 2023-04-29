package com.njdaeger.projectmanager.webapp.model.web;

import java.util.List;

public record UnmanagedWorldListResponse(List<UnmanagedDimensionResponse> worlds) {

    public record UnmanagedDimensionResponse() {

    }

    public record UnmanagedWorldResponse() {

    }

}
