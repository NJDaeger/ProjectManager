package com.njdaeger.projectmanager.discordbot;

import com.njdaeger.projectmanager.PMConfig;
import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.ProjectManagerTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ProjectManagerBotTask extends ProjectManagerTask {

    private static volatile ProjectManagerBot bot;
    private BukkitTask botTask;

    public ProjectManagerBotTask(ProjectManager plugin, PMConfig config) {
        super(plugin, config);
    }

    @Override
    public void onEnable() {
        this.botTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getLogger().info("Starting Discord bot...");
            try {
                ProjectManagerBotTask.bot = new ProjectManagerBot(plugin, config);
            } catch (Exception e) {
                plugin.getLogger().severe("There was a problem starting the ProjectManagerBot...");
                e.printStackTrace();
                return;
            }
            plugin.getLogger().info("Discord bot started!");
        });
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Disabling ProjectManagerBot");
        if (bot == null) throw new RuntimeException("Bot is null");
        else {
            bot.logout().block();
            botTask.cancel();
        }
    }
}
