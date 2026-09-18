package com.garcilso.rougher_environment.mixin;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeColors.class)
public interface BiomeColorsAccessor {
    
    @Mutable
    @Accessor("FOLIAGE_COLOR_RESOLVER")
    static void rougherenvironment$setFoliageColorResolver(ColorResolver newResolver) {
        
        throw new AssertionError();
    }
    
    @Mutable
    @Accessor("DRY_FOLIAGE_COLOR_RESOLVER")
    static void rougherenvironment$setDryFoliageColorResolver(ColorResolver newResolver) {
        
        throw new AssertionError();
    }
}