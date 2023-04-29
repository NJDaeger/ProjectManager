package com.njdaeger.projectmanager.webapp.controllers.impl;

import com.njdaeger.projectmanager.PMConfig;
import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.webapp.WebappInterface;
import com.njdaeger.projectmanager.webapp.annotations.ApiController;
import com.njdaeger.projectmanager.webapp.annotations.Get;
import com.njdaeger.projectmanager.webapp.controllers.IUserController;
import io.javalin.http.ContentType;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.UUID;

import static com.njdaeger.projectmanager.webapp.Util.error;

@ApiController(path = "v1/users")
public class UserControllerImpl implements IUserController {

    private final ProjectManager plugin;
    private WebappInterface app;
    private final PMConfig config;

    public UserControllerImpl(ProjectManager plugin, PMConfig config, WebappInterface app) {
        this.plugin = plugin;
        this.config = config;
        this.app = app;
    }

    @Override
    @Get(path = "skull")
    public Handler getUserHead() {
        return (ctx) -> {
            System.out.println(ctx.cookieMap());
            UUID uuid = UUID.fromString(ctx.cookie("user_id"));
            var session = app.peekSession(uuid);
            if (session.getPlayerHead() == null || session.getPlayerHead().length == 0) {
                var skinLink = (String)ctx.cookie("skinUrl");
                if (skinLink == null) {
                    ctx.result(error("No skin url found for that user.").toString());
                    ctx.status(HttpStatus.BAD_REQUEST);
                    return;
                }
                var url = new URL(skinLink);
                var img = ImageIO.read(url);
                var os = new ByteArrayOutputStream();
                ImageIO.write(img.getSubimage(8, 8, 8, 8), "png", os);
                session.setPlayerHead(os.toByteArray());
            }
            ctx.contentType(ContentType.IMAGE_PNG);
            ctx.result(session.getPlayerHead());
            ctx.status(HttpStatus.OK);
        };
    }
}
