package com.giorg.performance_optimizer.mixin.voxy;

import com.giorg.performance_optimizer.util.INativeRasterizer;
import me.cortex.voxy.client.core.model.bakery.SoftwareModelTextureBakery;
import me.cortex.voxy.client.core.model.bakery.SoftwareRasterizer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = SoftwareModelTextureBakery.class, remap = false)
public class MixinSoftwareModelTextureBakery {
    @Shadow private SoftwareRasterizer rasterizer;

    private long nativeAtlasPtr = 0L;

    /**
     * @author Antigravity
     * @reason Prevent OutOfMemoryError by buffering the massive 4GB texture atlas directly in aligned native memory.
     */
    @Overwrite
    private void _doSetupTexture(int glId) {
        if (this.nativeAtlasPtr != 0L) {
            MemoryUtil.nmemAlignedFree(this.nativeAtlasPtr);
            this.nativeAtlasPtr = 0L;
            if (this.rasterizer != null) {
                ((INativeRasterizer) this.rasterizer).setNativeSampler(0L, 0, 0);
            }
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, glId);
        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        long totalBytes = (long) width * (long) height * 4L;
        this.nativeAtlasPtr = MemoryUtil.nmemAlignedAlloc(32L, totalBytes);
        
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.nativeAtlasPtr);
        
        ((INativeRasterizer) this.rasterizer).setNativeSampler(this.nativeAtlasPtr, width, height);
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = "free", at = @org.spongepowered.asm.mixin.injection.At("HEAD"))
    private void onFree(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (this.nativeAtlasPtr != 0L) {
            MemoryUtil.nmemAlignedFree(this.nativeAtlasPtr);
            this.nativeAtlasPtr = 0L;
            if (this.rasterizer != null) {
                ((INativeRasterizer) this.rasterizer).setNativeSampler(0L, 0, 0);
            }
        }
    }
}
