package com.giorg.performance_optimizer.util;

public interface INativeRasterizer {
    void setNativeSampler(long pointer, int width, int height);
}
