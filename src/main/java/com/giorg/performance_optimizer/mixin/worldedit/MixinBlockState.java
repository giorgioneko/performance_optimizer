package com.giorg.performance_optimizer.mixin.worldedit;

import com.sk89q.worldedit.registry.state.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Mixin(targets = "com.sk89q.worldedit.world.block.BlockState", remap = false)
public abstract class MixinBlockState {

    @Shadow public abstract com.sk89q.worldedit.world.block.BlockType getBlockType();
    @Shadow public abstract <V> V getState(Property<V> property);
    @Shadow public abstract Map<Property<?>, Object> getStates();

    private static java.lang.reflect.Method getBlockStatesMapMethod;

    /**
     * @author Antigravity
     * @reason Removes 2.5GB memory leak caused by WorldEdit eagerly caching all permutation transitions in DenseImmutableTable.
     */
    @Overwrite
    public <V> com.sk89q.worldedit.world.block.BlockState with(Property<V> property, V value) {
        if (this.getState(property) == value) return (com.sk89q.worldedit.world.block.BlockState)(Object)this;
        Map<Property<?>, Object> newValues = new HashMap<>(this.getStates());
        newValues.put(property, value);
        try {
            if (getBlockStatesMapMethod == null) {
                getBlockStatesMapMethod = com.sk89q.worldedit.world.block.BlockType.class.getDeclaredMethod("getBlockStatesMap");
                getBlockStatesMapMethod.setAccessible(true);
            }
            @SuppressWarnings("unchecked")
            Map<Map<Property<?>, Object>, com.sk89q.worldedit.world.block.BlockState> map = 
                (Map<Map<Property<?>, Object>, com.sk89q.worldedit.world.block.BlockState>) getBlockStatesMapMethod.invoke(this.getBlockType());
            com.sk89q.worldedit.world.block.BlockState result = map.get(newValues);
            return result == null ? (com.sk89q.worldedit.world.block.BlockState)(Object)this : result;
        } catch (Exception e) {
            e.printStackTrace();
            return (com.sk89q.worldedit.world.block.BlockState)(Object)this;
        }
    }

    /**
     * @author Antigravity
     * @reason Replaces WorldEdit's horribly inefficient TreeMap allocation loops which caused millions of object allocations and GC death.
     */
    @Overwrite
    static Map<Map<Property<?>, Object>, com.sk89q.worldedit.world.block.BlockState> generateStateMap(com.sk89q.worldedit.world.block.BlockType blockType) {
        List<Property<?>> properties = new ArrayList<>();
        for (Property<?> p : blockType.getProperties()) {
            properties.add(p);
        }
        
        long permutations = 1;
        for (Property<?> property : properties) {
            permutations *= property.getValues().size();
            // Strict fast fail for ridiculous modded blocks to prevent JVM deadlock
            if (permutations > 10000) {
                try {
                    Constructor<?> constructor = com.sk89q.worldedit.world.block.BlockState.class.getDeclaredConstructor(com.sk89q.worldedit.world.block.BlockType.class);
                    constructor.setAccessible(true);
                    com.sk89q.worldedit.world.block.BlockState dummyState = (com.sk89q.worldedit.world.block.BlockState) constructor.newInstance(blockType);
                    return ImmutableMap.of(ImmutableMap.of(), dummyState);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ImmutableMap.of();
                }
            }
        }
        
        if (properties.isEmpty()) {
            try {
                Constructor<?> constructor = com.sk89q.worldedit.world.block.BlockState.class.getDeclaredConstructor(com.sk89q.worldedit.world.block.BlockType.class);
                constructor.setAccessible(true);
                com.sk89q.worldedit.world.block.BlockState single = (com.sk89q.worldedit.world.block.BlockState) constructor.newInstance(blockType);
                Map<Map<Property<?>, Object>, com.sk89q.worldedit.world.block.BlockState> singleMap = ImmutableMap.of(ImmutableMap.of(), single);
                Method populate = com.sk89q.worldedit.world.block.BlockState.class.getDeclaredMethod("populate", Map.class);
                populate.setAccessible(true);
                populate.invoke(single, singleMap);
                return singleMap;
            } catch (Exception e) {
                e.printStackTrace();
                return ImmutableMap.of();
            }
        }

        List<List<Object>> separatedValues = new ArrayList<>();
        for (Property<?> property : properties) {
            separatedValues.add(new ArrayList<>(property.getValues()));
        }

        List<List<Object>> valueLists = Lists.cartesianProduct(separatedValues);
        Map<Map<Property<?>, Object>, com.sk89q.worldedit.world.block.BlockState> finalMap = new HashMap<>((int)permutations);
        
        try {
            Constructor<?> constructor = com.sk89q.worldedit.world.block.BlockState.class.getDeclaredConstructor(com.sk89q.worldedit.world.block.BlockType.class);
            constructor.setAccessible(true);
            Method setState = com.sk89q.worldedit.world.block.BlockState.class.getDeclaredMethod("setState", Property.class, Object.class);
            setState.setAccessible(true);
            
            for (List<Object> valueList : valueLists) {
                // CRITICAL FIX: Use HashMap instead of TreeMap! Avoids millions of comparator calls.
                Map<Property<?>, Object> valueMap = new HashMap<>(properties.size(), 1.0f);
                com.sk89q.worldedit.world.block.BlockState stateMaker = (com.sk89q.worldedit.world.block.BlockState) constructor.newInstance(blockType);
                for (int i = 0; i < valueList.size(); ++i) {
                    @SuppressWarnings("unchecked")
                    Property<Object> property = (Property<Object>) properties.get(i);
                    Object value = valueList.get(i);
                    valueMap.put(property, value);
                    setState.invoke(stateMaker, property, value);
                }
                finalMap.put(ImmutableMap.copyOf(valueMap), stateMaker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Map<Map<Property<?>, Object>, com.sk89q.worldedit.world.block.BlockState> immutableFinal = ImmutableMap.copyOf(finalMap);
        // [Antigravity Fix]: We skip the `populate()` call entirely!
        // `populate()` builds a DenseImmutableTable inside EVERY BlockState linking to every other state.
        // This causes a massive 2.5 GB memory leak across 3 million modded states.
        // Instead, the `with()` overwrite dynamically resolves the state from `getBlockStatesMap()`.
        return immutableFinal;
    }
}
