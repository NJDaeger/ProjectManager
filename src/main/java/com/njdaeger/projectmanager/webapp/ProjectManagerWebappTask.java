package com.njdaeger.projectmanager.webapp;

import com.njdaeger.projectmanager.PMConfig;
import com.njdaeger.projectmanager.ProjectManager;
import com.njdaeger.projectmanager.ProjectManagerTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ProjectManagerWebappTask extends ProjectManagerTask {

    private static volatile ProjectManagerWebapp webapp;
    private BukkitTask webappTask;

    public ProjectManagerWebappTask(ProjectManager plugin, PMConfig config) {
        super(plugin, config);
    }

    @Override
    public void onEnable() {
        this.webappTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getLogger().info("Starting ProjectManager webapp...");
            try {
                ProjectManagerWebappTask.webapp = new ProjectManagerWebapp(plugin, config);
            } catch (Exception e) {
                plugin.getLogger().severe("There was a problem starting the ProjectManager webapp...");
                e.printStackTrace();
                return;
            }
            plugin.getLogger().info("Webapp started!");
        });
    }

    @Override
    public void onDisable() {
        webapp.shutdown();
        webappTask.cancel();
    }

    public WebappInterface getAppInterface() {
        return webapp;
    }

}
