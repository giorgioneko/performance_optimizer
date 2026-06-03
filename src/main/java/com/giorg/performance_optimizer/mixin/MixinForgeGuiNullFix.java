package com.giorg.performance_optimizer.mixin;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(value = ForgeGui.class, remap = false)
public class MixinForgeGuiNullFix {

    /**
     * Prevents rendering the HUD/GUI overlays if the player is still null.
     * This fixes NullPointerExceptions during world load where mods assume
     * the player is already fully initialized.
     */
    @Inject(method = "m_280421_", at = @At("HEAD"), cancellable = true)
    private void onRender(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) {
            ci.cancel();
        }
    }
}
