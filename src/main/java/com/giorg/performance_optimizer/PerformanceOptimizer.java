package com.giorg.performance_optimizer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("performance_optimizer")
public class PerformanceOptimizer {

    public static final Logger LOGGER = LogManager.getLogger("performance_optimizer");

    public PerformanceOptimizer() {
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Performance Optimizer initialized! Ready to improve game rendering and tick rates.");
    }
}
