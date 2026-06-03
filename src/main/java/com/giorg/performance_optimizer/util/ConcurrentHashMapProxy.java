package com.giorg.performance_optimizer.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A proxy that extends HashMap to satisfy JVM bytecode verification and cast requirements,
 * but internally delegates all operations to a thread-safe ConcurrentHashMap.
 * 
 * This prevents the HashMap$Node -> HashMap$TreeNode ClassCastException caused by C2ME
 * concurrent chunk loading triggering treeifyBin() concurrently.
 */
public class ConcurrentHashMapProxy<K, V> extends HashMap<K, V> {
    private final ConcurrentHashMap<K, V> delegate = new ConcurrentHashMap<>();

    @Override
    public V get(Object key) {
        return delegate.get(key);
    }

    @Override
    public V put(K key, V value) {
        return delegate.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }
    
    @Override
    public V putIfAbsent(K key, V value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegate.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return delegate.replace(key, value);
    }
}
