package com.giorg.performance_optimizer.mixin;

import com.ferreusveritas.dynamictrees.systems.poissondisc.LevelPoissonDiscProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import com.giorg.performance_optimizer.util.ConcurrentHashMapProxy;

/**
 * Replaces the internal chunkDiscs field (HashMap<ChunkPos, PoissonDiscChunkSet>)
 * in LevelPoissonDiscProvider with a ConcurrentHashMapProxy.
 *
 * The field is declared `private final HashMap chunkDiscs` in the original class.
 * @Mutable strips the final modifier so Mixin can reassign it safely inside the constructor.
 *
 * We assign our custom ConcurrentHashMapProxy which extends HashMap to satisfy JVM
 * type casting, but delegates to a thread-safe ConcurrentHashMap internally.
 */
@Mixin(value = LevelPoissonDiscProvider.class, remap = false)
public class MixinLevelPoissonDiscProvider {

    @Mutable
    @Final
    @Shadow
    private HashMap chunkDiscs;

    @Inject(
        method = "<init>(Lcom/ferreusveritas/dynamictrees/api/worldgen/RadiusCoordinator;)V",
        at = @At("RETURN")
    )
    private void performance_optimizer$replaceWithConcurrentMap(CallbackInfo ci) {
        this.chunkDiscs = new ConcurrentHashMapProxy<>();
    }
}
