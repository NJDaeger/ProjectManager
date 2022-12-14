package com.njdaeger.projectmanager.webapp;

import com.njdaeger.projectmanager.PMConfig;
import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.webapp.annotations.*;
import com.njdaeger.projectmanager.webapp.auth.AuthController;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import net.milkbowl.vault.permission.Permission;
import org.apache.log4j.BasicConfigurator;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
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
//        put("/home", "projectmanager.web.home");
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
            var uid = ctx.sessionAttribute("user_id");
            if (uid == null) return;//todo this should redirect
            var permission = routePermissionMap.get(ctx.path());
            if (permission == null) return;
            if (config.doVerboseLogging())
                plugin.getLogger().info(String.format("Permission check for [%s] accessing route [%s] requiring permission [%s]", uid, ctx.path(), permission));
            if (!sessionMap.containsKey((UUID)uid) || !sessionMap.get((UUID)uid).hasPermission(permission)) {
                if (config.doVerboseLogging())
                    plugin.getLogger().info(String.format("[UNAUTHORIZED] for [%s] accessing route [%s] requiring permission [%s]", uid, ctx.path(), permission));
                ctx.redirect("/", HttpStatus.UNAUTHORIZED);
            } else if (config.doVerboseLogging())
                plugin.getLogger().info(String.format("[AUTHORIZED] for [%s] accessing route [%s] requiring permission [%s]", uid, ctx.path(), permission));
        });

        this.controllers = new ArrayList<>();
        this.plugin = plugin;
        this.config = config;

        addController(new AuthController(plugin, config, this));
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
            Stream.of(controller.getClass().getDeclaredFields()).forEach(field -> {
                if (!isFieldAnnotatedRoute(field)) return;
                var handler = getFieldHandler(field, controller);
                var route = getRoute(field);
                if (handler == null) {
                    plugin.getLogger().warning("[" + controller.getClass().getCanonicalName() + "] Route " + baseRoute + route + " could not be registered. It does not return a type or a subtype of 'Handler'");
                    return;
                }
                addRoute(field, baseRoute, handler);
                loaded.addAndGet(1);
            });
            if (config.doVerboseLogging())
                plugin.getLogger().info("[" + controller.getClass().getCanonicalName() + "] Loaded " + loaded.get() + "/" + Stream.of(controller.getClass().getDeclaredFields()).filter(this::isFieldAnnotatedRoute).count() + " routes defined.");
        });
    }

    private void addRoute(Field field, String baseRoute, Handler handler) {
        var route = baseRoute + getRoute(field);
        if (field.isAnnotationPresent(Get.class)) app.get(route, handler);
        else if (field.isAnnotationPresent(Post.class)) app.post(route, handler);
        else if (field.isAnnotationPresent(Delete.class)) app.delete(route, handler);
        else if (field.isAnnotationPresent(Patch.class)) app.patch(route, handler);
        else throw new RuntimeException("Unknown request operation");

        if (field.isAnnotationPresent(com.njdaeger.projectmanager.webapp.annotations.Permission.class)) {
            var permission = field.getAnnotation(com.njdaeger.projectmanager.webapp.annotations.Permission.class).permission();
            routePermissionMap.put(route, permission);
        }
    }

    private String getRoute(Field field) {
        if (field.isAnnotationPresent(Get.class)) return field.getAnnotation(Get.class).path();
        else if (field.isAnnotationPresent(Post.class)) return field.getAnnotation(Post.class).path();
        else if (field.isAnnotationPresent(Delete.class)) return field.getAnnotation(Delete.class).path();
        else if (field.isAnnotationPresent(Patch.class)) return field.getAnnotation(Patch.class).path();
        else return null;
    }

    private Handler getFieldHandler(Field field, Object controller) {
        try {
            field.setAccessible(true);
            var val = field.get(controller);
            if (val instanceof Handler handler) return handler;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private boolean isFieldAnnotatedRoute(Field field) {
        return field.isAnnotationPresent(Get.class) || field.isAnnotationPresent(Delete.class) || field.isAnnotationPresent(Patch.class) | field.isAnnotationPresent(Post.class);
    }


}
