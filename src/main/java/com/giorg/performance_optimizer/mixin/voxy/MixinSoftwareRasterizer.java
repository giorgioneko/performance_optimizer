package com.giorg.performance_optimizer.mixin.voxy;

import me.cortex.voxy.client.core.model.bakery.SoftwareRasterizer;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import com.giorg.performance_optimizer.util.INativeRasterizer;

@Mixin(value = SoftwareRasterizer.class, remap = false)
public class MixinSoftwareRasterizer implements INativeRasterizer {
    @Shadow private int samplerWidth;
    @Shadow private int samplerHeight;

    private long nativeSamplerPtr = 0L;

    @Override
    public void setNativeSampler(long pointer, int width, int height) {
        this.nativeSamplerPtr = pointer;
        this.samplerWidth = width;
        this.samplerHeight = height;
    }

    /**
     * @author Antigravity
     * @reason Overwrites the sampling logic to read directly from a 64-bit native memory pointer, bypassing the 2GB Java limit.
     */
    @Overwrite
    private int sampleTexture(float u, float v) {
        int pu = Math.max(0, Math.min(Math.round(u * (float)this.samplerWidth - 0.5f), this.samplerWidth - 1));
        int pv = Math.max(0, Math.min(Math.round(v * (float)this.samplerHeight - 0.5f), this.samplerHeight - 1));
        
        if (this.nativeSamplerPtr != 0L) {
            long index = (long)this.samplerWidth * (long)pv + (long)pu;
            return MemoryUtil.memGetInt(this.nativeSamplerPtr + index * 4L);
        }
        
        return 0;
    }
}
