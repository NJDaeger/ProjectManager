package com.njdaeger.projectmanager.webapp;

import java.util.UUID;

public class WebSession {

    private boolean isLoggedIn;
    private long lastLoginTime;
    private final ProjectManagerWebapp app;
    private final long sessionCreationTime;
    private final UUID sessionOwner;
    private transient long otpCreation;
    private transient String otp;

    WebSession(UUID session, ProjectManagerWebapp app) {
        this.sessionCreationTime = System.currentTimeMillis();
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

    public void login(String token) {
        if (!TokenUtil.GENERATED_TOKENS.contains(token)) throw new RuntimeException("User token has not been properly generated.");
        else {
            this.isLoggedIn = true;
            this.lastLoginTime = System.currentTimeMillis();
            app.keyUidMap.put(token, sessionOwner);
            clearOTP();
        }
    }

    public void logout() {
        this.isLoggedIn = false;
        clearOTP();
        this.lastLoginTime = 0;
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

    public long getLastLoginTime() {
        return lastLoginTime;
    }
}
