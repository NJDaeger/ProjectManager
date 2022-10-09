package com.njdaeger.projectmanager.discordbot;

import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.webapp.WebSession;
import com.njdaeger.projectmanager.webapp.WebappInterface;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

public class LogoutCommand implements SlashCommand {

    private final WebappInterface app;
    private final ProjectManager plugin;
    private final String commandName;
    private final String description;

    public LogoutCommand(ProjectManager plugin, WebappInterface app) {
        this.app = app;
        this.plugin = plugin;
        this.commandName = "pmlogout";
        this.description = "Clear your current OTP if it was unused.";
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

        WebSession session = app.peekSession(uuid);
        if (session == null) {
            return event.reply()
                    .withEphemeral(true)
                    .withContent("You have no active session associated with your account.");//TODO custom message
        }
        session.logout(true);
        return event.reply()
                .withEphemeral(true)
                .withContent("You have been logged out of any logged in sessions."); //todo probably custom message
    }

    @Override
    public ApplicationCommandRequest getCommandRequest() {
        return ApplicationCommandRequest.builder()
                .name(commandName)
                .description(description)
                .build();
    }
}
