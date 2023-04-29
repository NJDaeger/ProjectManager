package com.njdaeger.projectmanager.utils;

import com.njdaeger.projectmanager.ProjectManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CacheMap<K, V> implements Map<K, V> {

    private final ProjectManager plugin;
    private final Map<K, TimestampedValue<V>> map;
    private final long timeout;

    public CacheMap(long timeoutInMillis) {
        this.plugin = ProjectManager.getPlugin(ProjectManager.class);
        this.map = new ConcurrentHashMap<>();
        this.timeout = timeoutInMillis;
    }

    @Override
    public int size() {
        pruneOldValues();
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        if (map.isEmpty()) return true;
        pruneOldValues();
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return map.values().stream().anyMatch(tv -> System.currentTimeMillis() - tv.getTimestamp() < timeout && tv.getValue().equals(value));
    }

    @Override
    public V get(Object key) {
        var res = map.get(key);
        if (res == null || System.currentTimeMillis() - res.getTimestamp() >= timeout) {
            plugin.verbose("CacheMap: Miss");
            map.remove(key);
            return null;
        } else {
            plugin.verbose("CacheMap: Hit");
            res.resetTimestamp();
            return res.getValue();
        }
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        var prev = map.put(key, new TimestampedValue<>(value));
        return prev == null ? null : prev.getValue();
    }

    @Override
    public V remove(Object key) {
        var prev = map.remove(key);
        return prev == null ? null : prev.getValue();
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        pruneOldValues();
        return map.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        pruneOldValues();
        return map.values().stream().map(TimestampedValue::getValue).toList();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        pruneOldValues();
        return map.entrySet().stream().map(entry -> new Entry<K, V>() {
            @Override
            public K getKey() {
                return entry.getKey();
            }

            @Override
            public V getValue() {
                return entry.getValue().getValue();
            }

            @Override
            public V setValue(V value) {
                throw new UnsupportedOperationException();
            }
        }).collect(Collectors.toSet());
    }

    private void pruneOldValues() {
        var removeKeys = new HashSet<K>();
        map.forEach((k, v) -> {
            if (System.currentTimeMillis() - v.getTimestamp() >= timeout) removeKeys.add(k);
        });
        removeKeys.forEach(map::remove);
    }

    public static class TimestampedValue<V> {

        private  long timestamp;
        private final V value;

        public TimestampedValue(V value) {
            this.timestamp = System.currentTimeMillis();
            this.value = value;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void resetTimestamp() {
            this.timestamp = System.currentTimeMillis();
        }

        public V getValue() {
            return value;
        }

    }
}
