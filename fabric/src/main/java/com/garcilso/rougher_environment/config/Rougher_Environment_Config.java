package com.garcilso.rougher_environment.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "rougher_environment")
public class Rougher_Environment_Config implements ConfigData {
    public GeneralSettings generalSettings = new GeneralSettings();
    public static class GeneralSettings {
        @ConfigEntry.Gui.Excluded
        public int Default_Color = 0xFFFFFF;
        
    }
    
    @ConfigEntry.Gui.CollapsibleObject
    public FoliageSettings foliageSettings = new FoliageSettings();
    public static class FoliageSettings{
        @ConfigEntry.Gui.Tooltip
        public float Foliage_Scale = 412f;
        @ConfigEntry.Gui.Tooltip
        public float Foliage_Strength = 0.475f;
        
        @ConfigEntry.Gui.Tooltip
        public float Dry_Foliage_Scale = 412f;
        @ConfigEntry.Gui.Tooltip
        public float Dry_Foliage_Strength = 0.475f;
        
        @ConfigEntry.Gui.Excluded
    public int Foliage_Color = 0x303800;
    }
    
    @ConfigEntry.Gui.CollapsibleObject
    public SandSettings sandSettings = new SandSettings();
    public static class SandSettings {
        
        @ConfigEntry.Gui.Tooltip
        public float Sand_Scale = 96f;
        @ConfigEntry.Gui.Tooltip
        public float Sand_Strength = 0.275f;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 29)
        public int Sand_Blend = 5;
        @ConfigEntry.Gui.Excluded
        public int Sand_Color = 0xA48282;
        
    }
    
    @ConfigEntry.Gui.CollapsibleObject
    public RedSandSettings redsandSettings = new RedSandSettings();
    public static class RedSandSettings {
        
        @ConfigEntry.Gui.Tooltip
        public float Red_Sand_Scale = 96f;
        @ConfigEntry.Gui.Tooltip
        public float Red_Sand_Strength = 0.825f;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 29)
        public int Red_Sand_Blend = 5;
        @ConfigEntry.Gui.Excluded
        public int Red_Sand_Color = 0xB87E2C;
        
    }
    
    @ConfigEntry.Gui.CollapsibleObject
    public EndStoneSettings endstoneSettings = new EndStoneSettings();
    public static class EndStoneSettings {
        
        @ConfigEntry.Gui.Tooltip
        public float End_Stone_Scale = 96f;
        @ConfigEntry.Gui.Tooltip
        public float End_Stone_Strength = 0.275f;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 29)
        public int End_Stone_Blend = 7;
        @ConfigEntry.Gui.Excluded
        public int End_Stone_Color = 0x220958;
        
    }
    
    @ConfigEntry.Gui.CollapsibleObject
    public BedrockSettings bedrockSettings = new BedrockSettings();
    public static class BedrockSettings {
        
        @ConfigEntry.Gui.Tooltip
        public float Bedrock_Scale = 196f;
        @ConfigEntry.Gui.Tooltip
        public float Bedrock_Strength = 0.425f;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 29)
        public int Bedrock_Blend = 3;
        @ConfigEntry.Gui.Excluded
        public int Bedrock_Color = 0x464646;
        
    }
    
    @ConfigEntry.Gui.CollapsibleObject
    public LavaSettings lavaSettings = new LavaSettings();
    public static class LavaSettings {
        
        @ConfigEntry.Gui.Tooltip
        public float Lava_Scale = 48f;
        @ConfigEntry.Gui.Tooltip
        public float Lava_Strength = 0.985f;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 29)
        public int Lava_Blend = 3;
        @ConfigEntry.Gui.Excluded
        public int Lava_Color = 0x2B2B2B;
        
    }
    
    @ConfigEntry.Gui.CollapsibleObject
    public NetherrackSettings netherrackSettings = new NetherrackSettings();
    public static class NetherrackSettings {
        
        @ConfigEntry.Gui.Tooltip
        public float Netherrack_Scale = 48f;
        @ConfigEntry.Gui.Tooltip
        public float Netherrack_Strength = 0.975f;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 29)
        public int Netherrack_Blend = 3;
        @ConfigEntry.Gui.Excluded
        public int Netherrack_Color = 0x2B0D22;
        
    }
    
    @ConfigEntry.Gui.CollapsibleObject
    public SoulSandsSettings soulsandsSettings = new SoulSandsSettings();
    public static class SoulSandsSettings {
        
        @ConfigEntry.Gui.Tooltip
        public float Soul_Sands_Scale = 32f;
        @ConfigEntry.Gui.Tooltip
        public float Soul_Sands_Strength = 0.975f;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 29)
        public int Soul_Sands_Blend = 5;
        @ConfigEntry.Gui.Excluded
        public int Soul_Sands_Color = 0x1B4646;
        
    }
    
    @ConfigEntry.Gui.CollapsibleObject
    public IcesSettings icesSettings = new IcesSettings();
    public static class IcesSettings {
        
        @ConfigEntry.Gui.Tooltip
        public float Ices_Scale = 64f;
        @ConfigEntry.Gui.Tooltip
        public float Ices_Strength = 0.275f;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 29)
        public int Ices_Blend = 3;
        @ConfigEntry.Gui.Excluded
        public int Ices_Color = 0xCCFEFF;
        
    }
}
