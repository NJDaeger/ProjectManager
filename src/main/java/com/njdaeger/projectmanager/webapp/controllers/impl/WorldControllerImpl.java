package com.njdaeger.projectmanager.webapp.controllers.impl;

import com.njdaeger.projectmanager.PMConfig;
import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.models.ManagedWorld;
import com.njdaeger.projectmanager.webapp.WebappInterface;
import com.njdaeger.projectmanager.webapp.annotations.ApiController;
import com.njdaeger.projectmanager.webapp.annotations.Get;
import com.njdaeger.projectmanager.webapp.annotations.Permission;
import com.njdaeger.projectmanager.webapp.annotations.Post;
import com.njdaeger.projectmanager.webapp.controllers.IWorldController;
import com.njdaeger.projectmanager.webapp.model.web.WorldListResponse;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.njdaeger.projectmanager.webapp.Util.*;

@ApiController(path = "v1/worlds")
public class WorldControllerImpl implements IWorldController {

    private final ProjectManager plugin;
    private WebappInterface app;
    private final PMConfig config;
    public WorldControllerImpl(ProjectManager plugin, PMConfig config, WebappInterface app) {
        this.plugin = plugin;
        this.config = config;
        this.app = app;
    }

    @Override
    @Get(path = "worldlist")
//    @Permission(permission = "plotman.web.registeredworldlist") //no permission listed because each world should have its own permission
    public Handler getManagedWorlds() {
        return (ctx) -> {
            ctx.future(async(() -> {
                var session = app.peekSession(ctx);
                if (session == null) {
                    ctx.result(json("message", "You are unauthorized for that action. You have no session  associated with your access attempt.").toString());
                    ctx.status(HttpStatus.UNAUTHORIZED);
                    return;
                }
                var worlds = plugin.getDataAccess().getWorldService().getWorlds();
                var result = new WorldListResponse(new ArrayList<>());
                worlds.stream().forEach(mw -> {
//                    var accessableDims = Arrays.stream(mw.managedDimensions()).filter(dim -> dim.accessPermission() == null || session.hasPermission(dim.accessPermission())).toList();
//                    if (accessableDims.size() != 0) {
//                        result.worlds().add(new WorldListResponse.ManagedWorldResponse(mw.worldId().toString(), mw.worldName(), accessableDims.stream().map(dim -> new WorldListResponse.ManagedDimensionResponse(dim.dimensionName(), 0, dim.accessPermission())).toList()));
//                    }
                });

                ctx.result(serializeJson(result).toString());
                ctx.status(HttpStatus.OK);
            }));
        };
    }

    @Override
    @Get(path = "unmanaged_worldlist")
    @Permission(permission = "plotman.admin.unmanaged-worldlist")
    public Handler getUnmanagedWorlds() {
        return (ctx) -> {
            ctx.future(async(() -> {

                var managedWorlds = plugin.getDataAccess().getWorldService().getWorlds().stream().map(ManagedWorld::worldId);
//                var worlds = Bukkit.getWorlds().stream().filter(world -> managedWorlds.noneMatch(mw -> mw.equals(world.getUID()))).map(w -> ).toList();

            }));
        };
    }

    @Override
    @Post(path = "register")
    @Permission(permission = "plotman.admin.registerworld")
    public Handler postRegisterWorld() {
        return null;
    }


}
