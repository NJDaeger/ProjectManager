package com.njdaeger.projectmanager;

public abstract class ProjectManagerTask {

    protected final ProjectManager plugin;
    protected final PMConfig config;

    protected ProjectManagerTask(ProjectManager plugin, PMConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    protected abstract void onEnable();

    protected abstract void onDisable();

}
