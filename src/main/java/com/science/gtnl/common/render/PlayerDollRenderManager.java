package com.science.gtnl.common.render;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PlayerDollRenderManager {

    public static boolean offlineMode = false;
    public static Set<String> BLACKLISTED_UUIDS = Sets.newConcurrentHashSet();
    public static Set<String> BLACKLISTED_NAMES = Sets.newConcurrentHashSet();
    public static Set<String> BLACKLISTED_SKIN_URLS = Sets.newConcurrentHashSet();
    public static Set<String> BLACKLISTED_CAPE_URLS = Sets.newConcurrentHashSet();
    public static Map<String, String> UUID_CACHE = new HashMap<>();

    public static class AsyncDownloader {

        public static Map<String, Future<?>> pendingTasks = new ConcurrentHashMap<>();
        public static ExecutorService executor = Executors.newFixedThreadPool(2);
        public static Map<String, Future<ResourceLocation>> pendingDownloads = new ConcurrentHashMap<>();

        public static ResourceLocation getTexture(String key, Callable<ResourceLocation> task) {
            Future<ResourceLocation> future = pendingDownloads.get(key);
            if (future != null) {
                try {
                    if (future.isDone()) {
                        return future.get();
                    }
                    return null;
                } catch (Exception e) {
                    pendingDownloads.remove(key);
                    return null;
                }
            }

            future = executor.submit(task);
            pendingDownloads.put(key, future);
            return null;
        }

        public static String getUUID(String username, Callable<String> task) {
            String cacheKey = "uuid_" + username.toLowerCase();
            Future<String> future = (Future<String>) pendingTasks.get(cacheKey);
            if (future != null) {
                try {
                    if (future.isDone()) {
                        return future.get();
                    }
                    return null;
                } catch (Exception e) {
                    pendingTasks.remove(cacheKey);
                    return null;
                }
            }
            future = executor.submit(task);
            pendingTasks.put(cacheKey, future);
            return null;
        }
    }

    public static String fetchUUID(String username) {
        String cacheKey = username.toLowerCase();

        if (BLACKLISTED_NAMES.contains(cacheKey)) {
            return null;
        }

        if (UUID_CACHE.containsKey(cacheKey)) {
            return UUID_CACHE.get(cacheKey);
        }

        return AsyncDownloader.getUUID(cacheKey, () -> {
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);

                if (connection.getResponseCode() == 204) {
                    BLACKLISTED_NAMES.add(cacheKey);
                    return null;
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String json = reader.lines()
                        .collect(Collectors.joining());
                    JsonObject jsonObject = new JsonParser().parse(json)
                        .getAsJsonObject();
                    String uuid = jsonObject.get("id")
                        .getAsString();
                    UUID_CACHE.put(cacheKey, uuid);
                    return uuid;
                }
            } catch (Exception e) {
                BLACKLISTED_NAMES.add(cacheKey);
                return null;
            }
        });
    }

}
