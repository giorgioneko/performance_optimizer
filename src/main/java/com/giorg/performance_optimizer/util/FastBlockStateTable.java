package com.giorg.performance_optimizer.util;

import com.google.common.collect.Table;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class FastBlockStateTable<R, C, V> implements Table<R, C, V> {
    private final Map<R, Map<C, V>> backingMap = new HashMap<>();

    public FastBlockStateTable() {}

    @Override public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) { return false; }
    @Override public boolean containsRow(@Nullable Object rowKey) { return false; }
    @Override public boolean containsColumn(@Nullable Object columnKey) { return false; }
    @Override public boolean containsValue(@Nullable Object value) { return false; }

    @Override
    public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
        Map<C, V> row = backingMap.get(rowKey);
        return row != null ? row.get(columnKey) : null;
    }

    @Override public boolean isEmpty() { return backingMap.isEmpty(); }
    @Override public int size() { return 0; }
    @Override public void clear() { backingMap.clear(); }

    @Override
    public V put(R rowKey, C columnKey, V value) {
        return backingMap.computeIfAbsent(rowKey, k -> new HashMap<>()).put(columnKey, value);
    }

    @Override public void putAll(Table<? extends R, ? extends C, ? extends V> table) { }
    @Override public V remove(@Nullable Object rowKey, @Nullable Object columnKey) { return null; }
    @Override public Map<C, V> row(R rowKey) { return backingMap.getOrDefault(rowKey, new HashMap<>()); }
    @Override public Map<R, V> column(C columnKey) { return null; }
    @Override public Set<Cell<R, C, V>> cellSet() { return null; }
    @Override public Set<R> rowKeySet() { return backingMap.keySet(); }
    @Override public Set<C> columnKeySet() { return null; }
    @Override public Collection<V> values() { return null; }
    @Override public Map<R, Map<C, V>> rowMap() { return backingMap; }
    @Override public Map<C, Map<R, V>> columnMap() { return null; }
}
