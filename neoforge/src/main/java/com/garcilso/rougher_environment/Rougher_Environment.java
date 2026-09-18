package com.garcilso.rougher_environment;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("rougher_environment")
public class Rougher_Environment {
    
    public Rougher_Environment(IEventBus modBus) {
        
        modBus.addListener(this::doClientStuff);
    }
    
    private void doClientStuff(final FMLClientSetupEvent event) {
        Rougher_Environment_Common.init();
    }
}
