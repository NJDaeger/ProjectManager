package com.njdaeger.projectmanager.discordbot;

import com.njdaeger.projectmanager.PMConfig;
import com.njdaeger.projectmanager.ProjectManager;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import discord4j.rest.RestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

class ProjectManagerBot {

    private final ProjectManager plugin;
    private final List<SlashCommand> commands;

    private final RestClient restClient;

    private final GatewayDiscordClient client;

    public ProjectManagerBot(ProjectManager plugin, PMConfig config) {
        GatewayDiscordClient client = DiscordClientBuilder.create(config.getBotToken()).build().login().block();

        if (client == null) throw new RuntimeException("Discord gateway client was null, the Project Manager Bot will not run.");

        this.client = client;
        this.restClient = client.getRestClient();
        this.plugin = plugin;
        this.commands = new ArrayList<>() {{
            add(new LoginCommand(plugin, plugin.getWebappInterface()));
            add(new LogoutCommand(plugin, plugin.getWebappInterface()));
        }};

        //just a quick ready message
        client.on(ReadyEvent.class, event ->
            Mono.fromRunnable(() -> {
                final User user = event.getSelf();
                plugin.getLogger().info("Logged in as " + user.getUsername() + "#" + user.getDiscriminator());
            }
        )).subscribe();

        //If we disconnect, we want to send the message as a warning in console
        client.on(DisconnectEvent.class, event ->
            Mono.fromRunnable(() -> {
                var cause = event.getCause();
                cause.ifPresent(throwable -> plugin.getLogger().warning("ProjectManagerBot disconnected for reason [" + throwable.getMessage() + "]"));
                if (cause.isEmpty()) plugin.getLogger().info("ProjectManagerBot disconnected.");
            })
        ).subscribe();

        plugin.getLogger().info("Invite this bot into your guild by following the link: " + generateInviteLink());

        registerCommands(client);
    }

    public Mono<Void> logout() {
        return client.logout();
    }

    private String generateInviteLink() {
        return "https://discord.com/oauth2/authorize?client_id=" + plugin.getPMConfig().getClientId() + "&permissions=274877908992&scope=bot";
    }

    private Mono<Void> commandHandler(ChatInputInteractionEvent event) {
        return Flux.fromIterable(commands)
                .filter(cmd -> cmd.getName().equalsIgnoreCase(event.getCommandName()))
                .next()
                .flatMap(cmd -> cmd.handle(event));
    }

    private void registerCommands(GatewayDiscordClient client) {
        var id = restClient.getApplicationId().block();
        if (id == null) throw new RuntimeException("Unable to register commands, ApplicationId was null.");

        restClient.getApplicationService().bulkOverwriteGlobalApplicationCommand(id, commands.stream().map(SlashCommand::getCommandRequest).toList())
                .doOnNext(cmd -> plugin.getLogger().info("Successfully registered global command '" + cmd.name() + "'"))
                .doOnError(e -> plugin.getLogger().warning("Unable to register global command... [" + e.getMessage() + "]"))
                .subscribe();

        //add the command handler listener
        client.on(ChatInputInteractionEvent.class, this::commandHandler)
                .then(client.onDisconnect())
                .subscribe();

    }



}
