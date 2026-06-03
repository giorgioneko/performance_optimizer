package com.giorg.performance_optimizer.mixin;

import com.chenjdy.modern_glass_doors.Config;
import com.chenjdy.modern_glass_doors.ModernGlassDoors;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashSet;

@Mixin(value = ModernGlassDoors.class, remap = false)
public class MixinModernGlassDoors {

    @Inject(method = "commonSetup", at = @At("HEAD"))
    private void performance_optimizer$onCommonSetup(FMLCommonSetupEvent event, CallbackInfo ci) {
        if (Config.items == null) {
            Config.items = Collections.synchronizedSet(new HashSet<>());
        }
    }
}
