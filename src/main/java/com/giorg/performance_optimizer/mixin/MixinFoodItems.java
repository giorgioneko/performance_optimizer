package com.giorg.performance_optimizer.mixin;

import lellson.foodexpansion.FoodExpansionConfig;
import lellson.foodexpansion.FoodItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(value = FoodItems.class, remap = false)
public class MixinFoodItems {

    @Inject(method = "increaseStackSizes", at = @At("HEAD"))
    private static void performance_optimizer$onIncreaseStackSizes(CallbackInfo ci) {
        if (FoodExpansionConfig.bowlStackSizeItems == null) {
            FoodExpansionConfig.bowlStackSizeItems = new ArrayList<>();
        }
    }
}
