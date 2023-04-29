package com.njdaeger.projectmanager.webapp;

import com.google.gson.*;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    public static CompletableFuture<String> getSkinFromUsername(String uuid) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid))
                .GET()
                .setHeader("Content-Type", "application/json")
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(body -> {
            var data = new JsonParser().parse(body.body());
            System.out.println(data.toString());
            if (data == null || data instanceof JsonNull) return null;
            var properties = data.getAsJsonObject().get("properties").getAsJsonArray();
            String skinUrl;
            for (JsonElement obj : properties) {
                if (obj.getAsJsonObject().get("name").getAsString().equalsIgnoreCase("textures")) {
                    var textureObj = new JsonParser().parse(new String(Base64.getDecoder().decode(obj.getAsJsonObject().get("value").getAsString())));
                    if (textureObj == null || textureObj instanceof JsonNull) return null;
                    var texture = textureObj.getAsJsonObject().get("textures");
                    if (texture == null || texture instanceof JsonNull) return null;
                    var skin = texture.getAsJsonObject().get("SKIN");
                    if (skin == null || skin instanceof JsonNull) return null;
                    var url = skin.getAsJsonObject().get("url");
                    if (url == null || url instanceof JsonNull) return null;
                    skinUrl = url.getAsString();
                    return skinUrl;
                }
            }
            return null;
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
            if (val instanceof Number num) obj.addProperty(key, num);
            else if (val instanceof Boolean bool) obj.addProperty(key, bool);
            else if (val instanceof Character chr) obj.addProperty(key, chr);
            else if (val.getClass().isArray()) obj.add(key, jsonArray((Object[])val));
            if (val instanceof Iterable<?> itr) obj.add(key, jsonArray(StreamSupport.stream(itr.spliterator(), false).toArray()));
            else if (val instanceof JsonElement elem) obj.add(key, elem);
            else obj.add(key, serializeJson(obj));
        }
        return obj;
    }

    public static JsonElement serializeJson(Object object) {
        if (object.getClass().isArray()) return jsonArray((Object[])object);
        if (object instanceof Iterable<?> i) return jsonArray(StreamSupport.stream(i.spliterator(), false).toArray());
        if (object.getClass().isRecord()) {
            var comp = Arrays.stream(object.getClass().getRecordComponents());
            var keyValuePairs = new ArrayList<>();
            comp.forEach(c -> {
                try {
                    keyValuePairs.add(c.getName());
                    keyValuePairs.add(c.getAccessor().invoke(object));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
            return json(keyValuePairs.toArray());
        }
        var fields = Arrays.stream(object.getClass().getDeclaredFields());
        var keyValuePairs = new ArrayList<>();
        fields.forEach(f -> {
            if (f.isAnnotationPresent(JsonIgnore.class)) return;
            var key = f.getName();
            if (f.isAnnotationPresent(JsonName.class)) key = f.getAnnotation(JsonName.class).name();

            try {
                keyValuePairs.add(key);
                keyValuePairs.add(f.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return json(keyValuePairs.toArray());
    }

    public static JsonArray jsonArray(Object... val) {
        var jsonArr = new JsonArray();
        if (val == null || val.length == 0) return jsonArr;
        Arrays.stream(val).forEach(o -> {
            if (o instanceof Number num) jsonArr.add(num);
            else if (o instanceof Boolean bool) jsonArr.add(bool);
            else if (o instanceof Character chr) jsonArr.add(chr);
            else if (o.getClass().isArray()) jsonArr.addAll(jsonArray((Object[]) o));
            else if (o instanceof Iterable<?> itr) jsonArr.addAll(jsonArray(StreamSupport.stream(itr.spliterator(), false).toArray()));
            else if (o instanceof JsonElement elem) jsonArr.add(elem);
            else jsonArr.add(serializeJson(o));
        });
        return jsonArr;
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
