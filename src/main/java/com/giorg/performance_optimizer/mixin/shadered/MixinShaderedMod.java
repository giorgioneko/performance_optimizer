package com.giorg.performance_optimizer.mixin.shadered;

import com.noodlegamer76.shadered.Config;
import com.noodlegamer76.shadered.ShaderedMod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

@Mixin(value = ShaderedMod.class, remap = false)
public class MixinShaderedMod {

    @Inject(method = "commonSetup", at = @At("HEAD"))
    private void beforeCommonSetup(FMLCommonSetupEvent event, CallbackInfo ci) {
        // If the ModConfigEvent hasn't fired or failed to initialize Config.items, prevent the NPE
        if (Config.items == null) {
            Config.items = Collections.emptySet();
        }
    }
}
