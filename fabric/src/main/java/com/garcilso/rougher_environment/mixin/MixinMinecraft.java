package com.garcilso.rougher_environment.mixin;

import com.garcilso.rougher_environment.Rougher_Environment_Common;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    
    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(GameConfig gameConfig, CallbackInfo ci) {
        
        Rougher_Environment_Common.init();
    }
    
}
