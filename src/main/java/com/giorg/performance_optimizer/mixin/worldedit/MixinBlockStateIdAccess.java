package com.giorg.performance_optimizer.mixin.worldedit;

import com.sk89q.worldedit.world.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.sk89q.worldedit.internal.block.BlockStateIdAccess", remap = false)
public class MixinBlockStateIdAccess {
    
    private static int registeredCount = 0;
    
    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void onRegister(BlockState blockState, int id, CallbackInfo ci) {
        registeredCount++;
        // Stop registering new states to WorldEdit's map if we reach 500,000 states.
        // This completely prevents the Int2ObjectOpenHashMap from rehashing to 4M+ capacity
        // and throwing a fatal OutOfMemoryError in fragmented heaps during server startup.
        if (registeredCount > 500000) {
            ci.cancel();
        }
    }
}
