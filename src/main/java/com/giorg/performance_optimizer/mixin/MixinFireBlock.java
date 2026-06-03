package com.giorg.performance_optimizer.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

@Mixin(FireBlock.class)
public abstract class MixinFireBlock {

    @Shadow private Object2IntMap<Block> igniteOdds;
    @Shadow private Object2IntMap<Block> burnOdds;

    /**
     * @author Antigravity
     * @reason Fix thread-safety issue when multiple mods add flammable blocks concurrently during FMLCommonSetupEvent.
     */
    @Overwrite
    public void setFlammable(Block pBlock, int pCatchOdds, int pBurnOdds) {
        synchronized (this) {
            this.igniteOdds.put(pBlock, pCatchOdds);
            this.burnOdds.put(pBlock, pBurnOdds);
        }
    }
}
