package com.giorg.performance_optimizer.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import waves.util.WaveHelpers;

@Mixin(value = WaveHelpers.class, remap = false)
public class MixinWaveHelpers {
    private static long performance_optimizer$lastUpdate = 0;

    @Inject(method = "updateCaches", at = @At("HEAD"), cancellable = true)
    private static void onUpdateCaches(Level level, Player player, CallbackInfo ci) {
        if (level != null) {
            long currentTicks = level.getGameTime();
            if (currentTicks - performance_optimizer$lastUpdate < 100) {
                ci.cancel();
            } else {
                performance_optimizer$lastUpdate = currentTicks;
            }
        }
    }
}
