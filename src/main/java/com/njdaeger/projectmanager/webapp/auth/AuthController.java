package com.njdaeger.projectmanager.webapp.auth;

import com.njdaeger.projectmanager.PMConfig;
import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.webapp.TokenUtil;
import com.njdaeger.projectmanager.webapp.Util;
import com.njdaeger.projectmanager.webapp.WebappInterface;
import com.njdaeger.projectmanager.webapp.annotations.ApiController;
import com.njdaeger.projectmanager.webapp.annotations.Get;
import com.njdaeger.projectmanager.webapp.annotations.Permission;
import com.njdaeger.projectmanager.webapp.annotations.Post;
import com.njdaeger.projectmanager.webapp.model.web.Login;
import com.njdaeger.projectmanager.webapp.model.web.VerifyAuthorizedModel;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.UUID;

import static com.njdaeger.projectmanager.webapp.Util.*;

@ApiController(path = "v1/auth")
public class AuthController {

    private final ProjectManager plugin;
    private WebappInterface app;
    private final PMConfig config;

    public AuthController(ProjectManager plugin, PMConfig config, WebappInterface app) {
        this.plugin = plugin;
        this.config = config;
        this.app = app;
    }

    @Post(path = "login")
    @Permission(permission = "projectmanager.web.login")
    public Handler postLogin = (ctx) -> {
        var login = ctx.bodyAsClass(Login.class);

        ctx.future(async(() -> {
            var uuid = await(Util.getUUIDFromUsername(login.username()));
            if (uuid == null) {
                ctx.result(error("No UUID was found with that username.").toString());
                ctx.status(HttpStatus.BAD_REQUEST);
                return;
            }
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
                ctx.result(json("message", "Success!").toString());
                ctx.status(HttpStatus.OK);
//                ctx.redirect("/home", HttpStatus.FOUND);
                ctx.sessionAttribute("user_id", uuid);
                session.login();
            } else {
                session.logout(false);
                ctx.result(error("You are not authorized for that action.").toString());
                ctx.sessionAttribute("user_id", null);
                ctx.status(HttpStatus.UNAUTHORIZED);
            }
        }));
    };


    @Post(path = "logout")
    @Permission(permission = "projectmanager.web.login") //if you can log in, you should be able to log out.
    public Handler postLogout = (ctx) -> {

    };

    @Post(path = "verify_authorized")
    public Handler verifyAuthorized = (ctx) -> {
        var route = ctx.bodyAsClass(VerifyAuthorizedModel.class);
        ctx.future(async(() -> {
            var uid = ctx.sessionAttribute("user_id");
            if (uid == null) return;//todo redirect
            var session = app.peekSession((UUID)uid);
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
