package com.njdaeger.projectmanager.discordbot;

import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.webapp.WebSession;
import com.njdaeger.projectmanager.webapp.WebappInterface;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

public class LoginCommand implements SlashCommand {

    private final WebappInterface app;
    private final ProjectManager plugin;
    private final String description;
    private final String commandName;

    public LoginCommand(ProjectManager plugin, WebappInterface app) {
        this.app = app;
        this.plugin = plugin;
        this.commandName = "pmlogin";
        this.description = "Get a one time password to log into the project management system.";
    }

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        var member = event.getInteraction().getMember();
        if (member.isEmpty()) {
            plugin.getLogger().warning("pmlogin was ran by an unknown member");
            return Mono.empty();
        }
        var uuid = plugin.getUserFromSnowflake(member.get().getId());

        if (uuid == null) {
            return event.reply()
                    .withEphemeral(true)
                    .withContent("You are not a registered user, please register and try again.");//TODO custom message
        }

        WebSession session = app.getOrCreateSession(uuid);
        var otp = session.getOTP();

//        Button.link("http://localhost:8080/discord_login?otp=" + otp, "Login");todo: button for logging in?

        var message = event.reply()//TODO do what this message says, also make it a custom message
                .withEphemeral(true)
                .withContent("Your One Time Password is ||``" + otp + "``||. Do not share this password with anyone. It will no longer be usable after a login");

        return message;
    }

    @Override
    public ApplicationCommandRequest getCommandRequest() {
        return ApplicationCommandRequest.builder()
                .name(commandName)
                .description(description)
                .build();
    }

}
