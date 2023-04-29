package com.njdaeger.projectmanager.webapp.controllers.impl;

import com.njdaeger.projectmanager.PMConfig;
import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.webapp.Util;
import com.njdaeger.projectmanager.webapp.WebappInterface;
import com.njdaeger.projectmanager.webapp.annotations.ApiController;
import com.njdaeger.projectmanager.webapp.annotations.Permission;
import com.njdaeger.projectmanager.webapp.annotations.Post;
import com.njdaeger.projectmanager.webapp.controllers.IAuthController;
import com.njdaeger.projectmanager.webapp.model.web.Login;
import com.njdaeger.projectmanager.webapp.model.web.VerifyAuthorizedModel;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.UUID;

import static com.njdaeger.projectmanager.webapp.Util.*;

@ApiController(path = "v1/auth")
public class AuthControllerImpl implements IAuthController {

    private final ProjectManager plugin;
    private WebappInterface app;
    private final PMConfig config;

    public AuthControllerImpl(ProjectManager plugin, PMConfig config, WebappInterface app) {
        this.plugin = plugin;
        this.config = config;
        this.app = app;
    }

    @Override
    @Post(path = "login")
    @Permission(permission = "plotman.web.login")
    public Handler postLogin() {
        return (ctx) -> {
            var login = ctx.bodyAsClass(Login.class);
            ctx.future(async(() -> {
                var uuid = await(Util.getUUIDFromUsername(login.username()));

                if (uuid == null) {
                    ctx.result(error("No UUID was found with that username.").toString());
                    ctx.status(HttpStatus.BAD_REQUEST);
                    return;
                }
                var skinUrlTask = Util.getSkinFromUsername(uuid.toString().replace("-", ""));
                var session = app.peekSession(uuid);
                if (session == null) {
                    ctx.result(error("No session has been started for that user.").toString());
                    ctx.status(HttpStatus.BAD_REQUEST);
                    return;
                }
                if (session.isLoggedIn()) {
                    ctx.result(error("You are already logged in at another location. Please log out of that location to log in here by running /logout in Discord.").toString());
                    ctx.status(HttpStatus.UNAUTHORIZED);
                    return;
                }
                if (session.isMatch(login.otp())) {
                    var skinUrl = await(skinUrlTask);
                    ctx.result(json("userId", uuid, "username", login.username(), "skinUrl", skinUrl).toString());
                    ctx.cookie("user_id", uuid.toString());
                    ctx.cookie("username", login.username());
                    ctx.cookie("skinUrl", skinUrl);
                    System.out.println(ctx.cookieMap());
                    plugin.getDataAccess().getUserService().createUser(uuid, login.username());
                    ctx.status(HttpStatus.OK);
                    session.login();
                } else {
                    session.logout(false);
                    ctx.result(error("You are not authorized for that action.").toString());
                    ctx.removeCookie("user_id");
                    ctx.removeCookie("username", null);
                    ctx.removeCookie("skinUrl", null);
                    ctx.status(HttpStatus.UNAUTHORIZED);
                }
            }));
        };
    }

    @Override
    @Post(path = "logout")
    @Permission(permission = "plotman.web.login") //if you can log in, you should be able to log out.
    public Handler postLogout() {
        return (ctx) -> {

        };
    }

    @Override
    @Post(path = "verify_authorized")
    public Handler verifyAuthorized() {
        return (ctx) -> {
            var route = ctx.bodyAsClass(VerifyAuthorizedModel.class);
            ctx.future(async(() -> {
                var uidString = ctx.cookie("user_id");
                if (uidString == null) {
                    ctx.result(json("message", "You are unauthorized for that action. You have no session associated with your access attempt.").toString());
                    ctx.status(HttpStatus.UNAUTHORIZED);
                    return;
                }
                var uid = UUID.fromString(uidString);
                var session = app.peekSession(uid);
                if (session == null) {
                    ctx.result(json("message", "You are unauthorized for that action. You have no session associated with your access attempt.").toString());
                    ctx.status(HttpStatus.UNAUTHORIZED);
                    return;
                }
                var permission = app.getRoutePermission(route.path());
                if (permission == null || session.hasPermission(app.getRoutePermission(route.path()))) {
                    ctx.result(json("message", "Authorized").toString());
                    ctx.status(HttpStatus.OK);
                }
                else {
                    ctx.result(json("message", "You are unauthorized for that action.").toString());
                    ctx.status(HttpStatus.UNAUTHORIZED);
                }
            }));
        };
    }

}
