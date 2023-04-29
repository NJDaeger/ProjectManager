package com.njdaeger.projectmanager.plugin;

import com.njdaeger.projectmanager.ProjectManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final ProjectManager plugin;

    public PlayerListener(ProjectManager plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        var userService = plugin.getDataAccess().getUserService();
        var user = userService.createUser(e.getPlayer().getUniqueId(), e.getPlayer().getName());
        if (!user.userName().equalsIgnoreCase(e.getPlayer().getName())) userService.updateUserName(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }


}
