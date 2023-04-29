package com.njdaeger.projectmanager.webapp;

import com.njdaeger.projectmanager.PMConfig;
import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.webapp.annotations.*;
import com.njdaeger.projectmanager.webapp.controllers.impl.AuthControllerImpl;
import com.njdaeger.projectmanager.webapp.controllers.impl.WorldControllerImpl;
import com.njdaeger.projectmanager.webapp.controllers.impl.UserControllerImpl;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import net.milkbowl.vault.permission.Permission;
import org.apache.log4j.BasicConfigurator;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

class ProjectManagerWebapp implements WebappInterface {

    private transient final Map<UUID, WebSession> sessionMap = new ConcurrentHashMap<>();
    //    transient final Map<String, UUID> keyUidMap = new ConcurrentHashMap<>();
    private final Permission permissionProvider;
    private final List<Object> controllers;
    private final ProjectManager plugin;

    private final PMConfig config;
    private final Javalin app;

    private final Map<String, String> routePermissionMap = new ConcurrentHashMap<>() {{
        put("/home", "projectmanager.web.home");
    }};

    public ProjectManagerWebapp(ProjectManager plugin, PMConfig config) {
        BasicConfigurator.configure();

        var rsp = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            this.permissionProvider = null;
            Bukkit.getLogger().warning("No Permission provider found by Vault. A default permission system will be enforced, where operators are allowed on pages deemed 'admin' and non operators are allowed on any other page.");
        } else this.permissionProvider = rsp.getProvider();

        this.app = Javalin.create(cfg -> {
            cfg.staticFiles.add("/static", Location.CLASSPATH);
//            cfg.staticFiles.add("/static");
            cfg.http.asyncTimeout = 30000L;
            cfg.http.defaultContentType = "application/json";
            cfg.routing.ignoreTrailingSlashes = true;
            cfg.routing.treatMultipleSlashesAsSingleSlash = true;
            cfg.requestLogger.http((ctx, ms) -> {
                if (config.doRequestLogging())
                    plugin.getLogger().info(String.format("[%f ms] %s | %s | %s", ms, ctx.method().name(), ctx.result(), ctx.fullUrl()));
            });
        });

        app.cfg.spaRoot.addFile("/", "/static/index.html", Location.CLASSPATH);
        app.exception(Exception.class, (e, ctx) -> e.printStackTrace());
        app.before((ctx) -> {
            var uidString = ctx.cookie("user_id");
            if (uidString == null) {
                plugin.verbose("BeforeHandler: No cookie stored for user [" + ctx.ip() + "]");
                return;
            }
            var uid = UUID.fromString(uidString);
            var permission = routePermissionMap.get(ctx.path());
            if (permission == null) {
                plugin.verbose("BeforeHandler: No permission mapped for [" + ctx.path() + "]");
                return;
            }
            plugin.verbose(String.format("BeforeHandler: Permission check for [%s] accessing route [%s] requiring permission [%s]", uid, ctx.path(), permission));
            if (!sessionMap.containsKey(uid) || !sessionMap.get(uid).hasPermission(permission)) {
                plugin.verbose(String.format("BeforeHandler: [UNAUTHORIZED] for [%s] accessing route [%s] requiring permission [%s]", uid, ctx.path(), permission));
            } else {
                boolean loggedIn = sessionMap.get(uid).isLoggedIn();
                if (ctx.path().contentEquals("/v1/auth/login") || loggedIn) plugin.verbose(String.format("BeforeHandler: [AUTHORIZED] for [%s] accessing route [%s] requiring permission [%s]", uid, ctx.path(), permission));
                else plugin.verbose(String.format("BeforeHandler: [UNAUTHORIZED] for [%s] accessing route [%s] requiring permission [%s] - User is not logged in.", uid, ctx.path(), permission));
            }
        });

        this.controllers = new ArrayList<>();
        this.plugin = plugin;
        this.config = config;

        addController(new AuthControllerImpl(plugin, config, this));
        addController(new UserControllerImpl(plugin, config, this));
        addController(new WorldControllerImpl(plugin, config, this));
        loadControllers(controllers);

        app.start(config.getWebappPort());
    }

    @Override
    public String getRoutePermission(String route) {
        return routePermissionMap.get(route);
    }

    @Override
    public WebSession peekSession(UUID userId) {
        return sessionMap.get(userId);
    }

    @Override
    public WebSession peekSession(Context ctx) {
        var uidString = ctx.cookie("user_id");
        if (uidString == null) return null;
        var uid = UUID.fromString(uidString);
        return peekSession(uid);
    }

    @Override
    public WebSession getOrCreateSession(UUID userId) {
        var session = sessionMap.get(userId);
        if (session != null) {
            if (config.getSessionExpireTime() > 0 && ((config.getSessionExpireTime() + session.getSessionCreationTime()) - System.currentTimeMillis()) <= 0) {
                session = new WebSession(userId, this, permissionProvider);
                sessionMap.put(userId, session);
            }
        } else {
            session = new WebSession(userId, this, permissionProvider);
            sessionMap.put(userId, session);
        }
        return session;
    }


    /**
     * Shutdown the javalin webapp
     */
    @Override
    public void shutdown() {
        app.stop();
    }

    void clearSession(UUID user) {
        sessionMap.remove(user);
    }

    private void addController(Object controller) {
        controllers.add(controller);
    }

    private void loadControllers(List<Object> controllers) {
        controllers.forEach(controller -> {
            if (!controller.getClass().isAnnotationPresent(ApiController.class)) {
                plugin.getLogger().warning("ApiController annotation not present on " + controller.getClass().getName() + ". No routes from this class will be loaded.");
                return;
            }

            AtomicInteger loaded = new AtomicInteger();
            var baseRoute = "/" + controller.getClass().getAnnotation(ApiController.class).path() + "/";
            Stream.of(controller.getClass().getDeclaredMethods()).forEach(method -> {
                if (!isMethodAnnotatedRoute(method)) return;
                var handler = getMethodHandler(method, controller);
                var route = getRoute(method);
                if (handler == null) {
                    plugin.getLogger().warning("[" + controller.getClass().getCanonicalName() + "] Route " + baseRoute + route + " could not be registered. It does not return a type or a subtype of 'Handler'");
                    return;
                }
                addRoute(method, baseRoute, handler);
                loaded.addAndGet(1);
            });
            if (config.doVerboseLogging())
                plugin.getLogger().info("[" + controller.getClass().getCanonicalName() + "] Loaded " + loaded.get() + "/" + Stream.of(controller.getClass().getDeclaredMethods()).filter(this::isMethodAnnotatedRoute).count() + " routes defined.");
        });
    }

    private void addRoute(Method method, String baseRoute, Handler handler) {
        var route = baseRoute + getRoute(method);
        if (method.isAnnotationPresent(Get.class)) app.get(route, handler);
        else if (method.isAnnotationPresent(Post.class)) app.post(route, handler);
        else if (method.isAnnotationPresent(Delete.class)) app.delete(route, handler);
        else if (method.isAnnotationPresent(Patch.class)) app.patch(route, handler);
        else throw new RuntimeException("Unknown request operation");

        if (method.isAnnotationPresent(com.njdaeger.projectmanager.webapp.annotations.Permission.class)) {
            var permission = method.getAnnotation(com.njdaeger.projectmanager.webapp.annotations.Permission.class).permission();
            routePermissionMap.put(route, permission);
        }
    }

    private String getRoute(Method method) {
        if (method.isAnnotationPresent(Get.class)) return method.getAnnotation(Get.class).path();
        else if (method.isAnnotationPresent(Post.class)) return method.getAnnotation(Post.class).path();
        else if (method.isAnnotationPresent(Delete.class)) return method.getAnnotation(Delete.class).path();
        else if (method.isAnnotationPresent(Patch.class)) return method.getAnnotation(Patch.class).path();
        else return null;
    }

    private Handler getMethodHandler(Method method, Object controller) {
        try {
            method.setAccessible(true);
            var val = method.invoke(controller);
            if (val instanceof Handler handler) return handler;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private boolean isMethodAnnotatedRoute(Method method) {
        return method.isAnnotationPresent(Get.class) || method.isAnnotationPresent(Delete.class) || method.isAnnotationPresent(Patch.class) | method.isAnnotationPresent(Post.class);
    }


}
