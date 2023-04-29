package com.njdaeger.projectmanager.webapp;

import io.javalin.http.Context;

import java.util.UUID;

public interface WebappInterface {

    /**
     * Peek at a web session
     * @param userId The UUID of the session owner
     * @return The found session, or null if no session has been created yet.
     */
    WebSession peekSession(UUID userId);

    /**
     * Peek at a web session
     * @param ctx The javalin handler context
     * @return The found session, or null if no session has been created yet.
     */
    WebSession peekSession(Context ctx);

    /**
     * Get or create a new web session given a userId. If the session was not found, a new session is created and returned. If a session was found, it is checked for expiration- and if it is expired, a new one is created, otherwise, the found session is returned.
     * @param userId The UUID of the user who is creating the session
     * @return the found, or newly created, web session
     */
    WebSession getOrCreateSession(UUID userId);

    /**
     * Shut down the web server
     */
    void shutdown();

    /**
     * Get the permission required for a given route
     * @param route The route to check permission for
     * @return The permission required to access the route
     */
    String getRoutePermission(String route);

}
