package com.njdaeger.projectmanager.discordbot;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

public interface SlashCommand {

    String getName();

    String getDescription();

    Mono<Void> handle(ChatInputInteractionEvent event);

    ApplicationCommandRequest getCommandRequest();

}
