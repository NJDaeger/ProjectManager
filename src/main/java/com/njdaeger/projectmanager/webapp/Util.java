package com.njdaeger.projectmanager.webapp;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class Util {

    public static CompletableFuture<UUID> getUUIDFromUsername(String username) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
                .GET()
                .setHeader("Content-Type", "application/json")
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(body -> {
            var data = new JsonParser().parse(body.body());
            if (data == null || data instanceof JsonNull) return null;
            UUID userId;
            try {
                userId =  UUID.fromString(data.getAsJsonObject().get("id").getAsString().replaceAll("(.{8})(.{4})(.{4})(.{4})(.+)", "$1-$2-$3-$4-$5"));
            } catch (IllegalArgumentException e) {
                return null;
            }
            return userId;
        });
    }

    public static <T> T await(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T await(Future<T> future, long timeout) {
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static Supplier<CompletableFuture<?>> async(Runnable runnable) {
        return () -> CompletableFuture.runAsync(runnable);
    }

    public static JsonObject json(Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) throw new RuntimeException("KeyValue json mapping does not contain same amount of keys and values");
        var obj = new JsonObject();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            var key = keyValuePairs[i].toString();
            var val = keyValuePairs[i + 1];
            if (val instanceof Number) obj.addProperty(key, (Number) val);
            else if (val instanceof Boolean) obj.addProperty(key, (Boolean) val);
            else if (val instanceof Character) obj.addProperty(key, (Character) val);
            else obj.addProperty(key, val.toString());
        }
        return obj;
    }

//    public static Object jsonObj(Object... keyValuePairs) {
//        //todo generate class?
//
//    }

    public static JsonObject error(String message) {
        return json("error", message);
    }

//    public static Object errorObj(String message) {
//        return json()
//    }

}
