package com.njdaeger.projectmanager.webapp;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;

import java.util.UUID;

public class WebSession {

    private boolean isLoggedIn;
    private long lastLoginTime;
    private final ProjectManagerWebapp app;
    private final long sessionCreationTime;
    private final UUID sessionOwner;
    private transient long otpCreation;
    private transient String otp;
    private final Permission permissionProvider;
    private byte[] playerHead;

    WebSession(UUID session, ProjectManagerWebapp app, Permission permissionProvider) {
        this.sessionCreationTime = System.currentTimeMillis();
        this.permissionProvider = permissionProvider;
        this.sessionOwner = session;
        this.isLoggedIn = false;
        this.app = app;
    }

    public void clearOTP() {
        this.otpCreation = -1;
        this.otp = null;
    }

    public long getOTPCreationTimeMillis() {
        return otpCreation;
    }

    public boolean hasPermission(String permission) {
        var player = Bukkit.getOfflinePlayer(sessionOwner);
        if (player.getName() == null) return false;
        else {//could be a problem if permission provider does not support null world...
            return player.isOp() || permissionProvider.playerHas(null, player, permission);
        }
    }

    public boolean isMatch(String otherOtp) {
        if (this.otp == null || otherOtp == null) return false;
        if (this.otp.equals(otherOtp)) {
            clearOTP();
            return true;
        } else {
            clearOTP();
            return false;
        }
    }

    public boolean login() {
        if (isLoggedIn) return false;
        this.isLoggedIn = true;
        this.lastLoginTime = System.currentTimeMillis();
        clearOTP();
        return true;
    }

    public boolean logout(boolean hardLogout) {
        if (!isLoggedIn) return false;
        this.isLoggedIn = false;
        clearOTP();
        if (hardLogout) app.clearSession(sessionOwner);
        return true;
    }

    public boolean isLoggedIn() {
        return this.isLoggedIn;
    }

    public String getOTP() {
        this.otpCreation = System.currentTimeMillis();
        this.otp = UUID.randomUUID().toString().replace("-", "");
        return this.otp;
    }

    public UUID getSessionOwner() {
        return sessionOwner;
    }

    public long getSessionCreationTime() {
        return sessionCreationTime;
    }

    public byte[] getPlayerHead() {
        return playerHead;
    }

    public void setPlayerHead(byte[] imageData) {
        this.playerHead = imageData;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }
}
