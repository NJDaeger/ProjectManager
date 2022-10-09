package com.njdaeger.projectmanager.webapp;

import java.util.UUID;

public interface WebappInterface {

    /**
     * Peek at a web session
     * @param userId The UUID of the session owner
     * @return The found session, or null if no session has been created yet.
     */
    WebSession peekSession(UUID userId);

    /**
     * Get or create a new web session given a userId. If the session was not found, a new session is created and returned. If a session was found, it is checked for expiration- and if it is expired, a new one is created, otherwise, the found session is returned.
     * @param userId The UUID of the user who is creating the session
     * @return the found, or newly created, web session
     */
    WebSession getOrCreateSession(UUID userId);
//
//
//    /**
//     * Peek at a web session by its session token
//     * @param token This sessions web token
//     * @return The found session, or null if no session has a matching token
//     */
//    WebSession peekSessionByToken(String token);

    void shutdown();

    String getRoutePermission(String route);

}
