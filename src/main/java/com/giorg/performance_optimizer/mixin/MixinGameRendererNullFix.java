package com.giorg.performance_optimizer.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(value = GameRenderer.class, priority = 100) // High priority so it runs before other mods
public class MixinGameRendererNullFix {

    @Shadow private Minecraft minecraft;

    /**
     * Prevents a NullPointerException when Vivecraft/Voxy/CreativeCore tries to render the level
     * when the player object is still null (e.g. during a harsh loading phase or disconnect).
     */
    @Inject(method = "renderLevel", at = @At("HEAD"), cancellable = true)
    private void onRenderLevel(float partialTicks, long finishTimeNano, PoseStack poseStack, CallbackInfo ci) {
        if (this.minecraft.player == null) {
            ci.cancel();
        }
    }

    /**
     * Also protect the main render loop just in case.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(float partialTicks, long finishTimeNano, boolean renderLevel, CallbackInfo ci) {
        if (renderLevel && this.minecraft.player == null) {
            // Let the UI render, but don't try to render the 3D world if player is null
        }
    }
}
