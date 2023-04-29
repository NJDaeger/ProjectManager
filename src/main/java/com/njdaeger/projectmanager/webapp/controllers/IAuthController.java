package com.njdaeger.projectmanager.webapp.controllers;

import io.javalin.http.Handler;

public interface IAuthController {

    Handler postLogin();

    Handler postLogout();

    Handler verifyAuthorized();

}
