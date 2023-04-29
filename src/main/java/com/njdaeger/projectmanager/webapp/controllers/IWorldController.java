package com.njdaeger.projectmanager.webapp.controllers;

import io.javalin.http.Handler;

public interface IWorldController {

    Handler getManagedWorlds();

    Handler getUnmanagedWorlds();

    Handler postRegisterWorld();

}
