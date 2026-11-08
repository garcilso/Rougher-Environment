package com.garcilso.rougher_environment;

import com.garcilso.rougher_environment.config.Rougher_Environment_Config;
import com.garcilso.rougher_environment.mixin.BiomeColorsAccessor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NoiseStack;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.lang.Math.clamp;
import static java.lang.Math.pow;

public class Rougher_Environment_Common {
    private static final Rougher_Environment_Config.GeneralSettings generalSettings = Rougher_Environment.getConfig().generalSettings;
    private static final Rougher_Environment_Config.FoliageSettings foliageSettings = Rougher_Environment.getConfig().foliageSettings;
    private static final Rougher_Environment_Config.SandSettings sandSettings = Rougher_Environment.getConfig().sandSettings;
    private static final Rougher_Environment_Config.RedSandSettings redsandSettings = Rougher_Environment.getConfig().redsandSettings;
    private static final Rougher_Environment_Config.EndStoneSettings endstoneSettings = Rougher_Environment.getConfig().endstoneSettings;
    private static final Rougher_Environment_Config.BedrockSettings bedrockSettings = Rougher_Environment.getConfig().bedrockSettings;
    private static final Rougher_Environment_Config.LavaSettings lavaSettings = Rougher_Environment.getConfig().lavaSettings;
    private static final Rougher_Environment_Config.NetherrackSettings netherrackSettings = Rougher_Environment.getConfig().netherrackSettings;
    private static final Rougher_Environment_Config.SoulSandsSettings soulsandsSettings = Rougher_Environment.getConfig().soulsandsSettings;
    private static final Rougher_Environment_Config.IcesSettings icesSettings = Rougher_Environment.getConfig().icesSettings;
    
    public static final int NOISE_OCTAVES = 2;
    public static final int NOISE_OCTAVES_STRONGER = 3;
    public static final List<Integer> OCTAVES = IntStream.rangeClosed(0, NOISE_OCTAVES).boxed().toList();
    public static final List<Integer> OCTAVES_STRONGER = IntStream.rangeClosed(0, NOISE_OCTAVES_STRONGER).boxed().toList();
    private static final Function<String, Noise> NOISE_GENERATOR = s -> {
        WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(s.hashCode()));
        NoiseStack.Builder builder = NoiseStack.builder();
        for(Integer octave : OCTAVES) {
            builder.add(new SimplexNoise(random, true), octave, (1 + octave) / 7f);
        }
        return builder.build();
    };
    private static final Function<String, Noise> NOISE_GENERATOR_STRONGER = s -> {
        WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(s.hashCode()));
        NoiseStack.Builder builder = NoiseStack.builder();
        for(Integer octave : OCTAVES_STRONGER) {
            builder.add(new SimplexNoise(random, true), octave, (1 + octave) / 7f);
        }
        return builder.build();
    };

    public static final Noise FOLIAGE_NOISE = NOISE_GENERATOR_STRONGER.apply("FOLIAGE_NOISE");
    public static final Noise DRY_FOLIAGE_NOISE = NOISE_GENERATOR_STRONGER.apply("DRY_FOLIAGE_NOISE");
    public static final Noise BLOCK_NOISE = NOISE_GENERATOR.apply("BLOCK_NOISE");
    public static final Noise LIQUID_NOISE = NOISE_GENERATOR.apply("LIQUID_NOISE");
    
    public static final ColorResolver FOLIAGE_RESOLVER = Util.make(() -> {
        final var baseResolver = BiomeColors.FOLIAGE_COLOR_RESOLVER;
        return (biome, x, z) -> modifyColour(FOLIAGE_NOISE, baseResolver, biome, x, z, foliageSettings.Foliage_Scale, foliageSettings.Foliage_Strength);
    });
    public static final ColorResolver DRY_FOLIAGE_RESOLVER = Util.make(() -> {
        final var baseResolver = BiomeColors.DRY_FOLIAGE_COLOR_RESOLVER;
        return (biome, x, z) -> modifyColour(DRY_FOLIAGE_NOISE, baseResolver, biome, x, z, foliageSettings.Dry_Foliage_Scale, foliageSettings.Dry_Foliage_Strength);
    });
    
    public static void init() {
        
        BiomeColorsAccessor.rougherenvironment$setFoliageColorResolver(Rougher_Environment_Common.FOLIAGE_RESOLVER);
        BiomeColorsAccessor.rougherenvironment$setDryFoliageColorResolver(Rougher_Environment_Common.DRY_FOLIAGE_RESOLVER);
        
    }
    
    private static int modifyColour(Noise generator, ColorResolver resolver, Biome biome, double x, double z, double scale, double darkness) {
        
        final int base = resolver.getColor(biome, x, z);
        double value = generator.get(x / scale, z / scale);
        value = curve(0, 1, remap(value, -((1 << NOISE_OCTAVES) - 1), (1 << NOISE_OCTAVES) - 1, 0, 1)) * darkness;
        return blend(base, foliageSettings.Foliage_Color, (float) (value));
    }
    
    public static double remap(final double value, final double currentLow, final double currentHigh, final double newLow, final double newHigh) {
        
        return newLow + (value - currentLow) * (newHigh - newLow) / (currentHigh - currentLow);
    }
    
    private static float getRed(final int hex) {
        
        return ((hex >> 16) & 0xFF) / 255f;
    }
    
    private static float getGreen(final int hex) {
        
        return ((hex >> 8) & 0xFF) / 255f;
    }
    
    private static float getBlue(final int hex) {
        
        return ((hex) & 0xFF) / 255f;
    }
    
    private static float getAlpha(final int hex) {
        
        return ((hex >> 24) & 0xff) / 255f;
    }
    
    private static float[] getARGB(final int hex) {
        
        return new float[] {getAlpha(hex), getRed(hex), getGreen(hex), getBlue(hex)};
    }
    
    private static int toInt(final float[] argb) {
        
        final int r = (int) Math.floor(argb[1] * 255) & 0xFF;
        final int g = (int) Math.floor(argb[2] * 255) & 0xFF;
        final int b = (int) Math.floor(argb[3] * 255) & 0xFF;
        final int a = (int) Math.floor(argb[0] * 255) & 0xFF;
        return (a << 24) + (r << 16) + (g << 8) + (b);
    }
    
    public static double curve(final double start, final double end, double amount) {
        
        amount = Mth.clamp(amount, 0, 1);
        amount = Mth.clamp((amount - start) / (end - start), 0, 1);
        return Mth.clamp(0.5 + 0.5 * Math.sin(Math.cos(Math.PI * Math.tan(90 * amount))) * Math.cos(Math.sin(Math.tan(amount))), 0, 1);
    }
    
    public static int blend(final int color1, final int color2, final float ratio) {
        
        final float ir = 1.0f - ratio;
        
        final float[] rgb1 = getARGB(color2);
        final float[] rgb2 = getARGB(color1);
        
        return toInt(new float[] {rgb1[0] * ratio + rgb2[0] * ir, rgb1[1] * ratio + rgb2[1] * ir, rgb1[2] * ratio + rgb2[2] * ir, rgb1[3] * ratio + rgb2[3] * ir});
    }
    
    //Rougher Environment "Original" code begins here
    
    private static int oddEven(int num) {
        if (num % 2 == 0) {
            num = num + 1;
        }
        
        return num;
    }
    
    private static int blendedColor(int color, int colorBlend, int x, int z, int distance, Noise generator, float scale, float darkness) {
        if (darkness != 0) {
            int distanceFixed = oddEven(distance); // Make sure it's an odd number, should've done the yandare dev method
            float scaleFixed = clamp(scale, 1f, 4096f); // Can't have people set it to 89896739675 and then wonder why it's sh*t
            float strengthFixed = clamp(darkness, 0.05f, 0.995f); // Values of 1 sometimes caused problems so here's this
            
            if (distanceFixed > 1) {
                int length = (int) ((pow(distanceFixed, 2)) + 1); // The +1 is necessary trust (It prevents strange blends)
                
                float A = getAlpha(color);
                float R = getRed(color);
                float G = getGreen(color);
                float B = getBlue(color);
                
                int CornerX = x - distanceFixed / 2;
                int CornerZ = z - distanceFixed / 2;

                for(int Blockx = 0; Blockx < distanceFixed; Blockx++) {
                    for(int Blockz = 0; Blockz < distanceFixed; Blockz++) {
                        int BlockColor = modifyColourNoBiome(generator, color, colorBlend, CornerX + Blockx, CornerZ + Blockz, scaleFixed, strengthFixed);
                        
                        A += getAlpha(BlockColor);
                        R += getRed(BlockColor);
                        G += getGreen(BlockColor);
                        B += getBlue(BlockColor);
                    }
                }
                
                int blendColor = toInt(new float[] {A / length, R / length, G / length, B / length});
                
                if(blendColor > color) { // Some bullsh*t these two
                    blendColor = color;
                }
                if(blendColor < colorBlend) { // AI could never
                    blendColor = colorBlend;
                }
                
                return blendColor;
            } else  {
                return modifyColourNoBiome(generator, color, colorBlend, x, z, scaleFixed, strengthFixed);
            }
        } else {
            return color;
        }
    }
    
    private static int modifyColourNoBiome(Noise generator, int color, int colorBlend, double x, double z, double scale, double darkness) {
        double value = generator.get(x / scale, z / scale);
        value = curve(0, 1, remap(value, -((1 << NOISE_OCTAVES) - 1), (1 << NOISE_OCTAVES) - 1, 0, 1)) * darkness;
        return blend(color,  colorBlend, (float) (value));
    }
    
    public static final ColorResolver SAND_RESOLVER = Util.make(() -> {
        return (biome, x, z) -> blendedColor(generalSettings.Default_Color, sandSettings.Sand_Color, (int) x, (int) z,
                sandSettings.Sand_Blend, BLOCK_NOISE, sandSettings.Sand_Scale, sandSettings.Sand_Strength);
    });
    
    public static final ColorResolver RED_SAND_RESOLVER = Util.make(() -> {
        return (biome, x, z) -> blendedColor(generalSettings.Default_Color, redsandSettings.Red_Sand_Color, (int) x, (int) z,
                redsandSettings.Red_Sand_Blend, BLOCK_NOISE, redsandSettings.Red_Sand_Scale, redsandSettings.Red_Sand_Strength);
    });
    
    public static final ColorResolver END_STONE_RESOLVER = Util.make(() -> {
        return (biome, x, z) -> blendedColor(generalSettings.Default_Color, endstoneSettings.End_Stone_Color, (int) x, (int) z,
                endstoneSettings.End_Stone_Blend, BLOCK_NOISE, endstoneSettings.End_Stone_Scale, endstoneSettings.End_Stone_Strength);
    });
    
    public static final ColorResolver BEDROCK_RESOLVER = Util.make(() -> {
        return (biome, x, z) -> blendedColor(generalSettings.Default_Color, bedrockSettings.Bedrock_Color, (int) x, (int) z,
                bedrockSettings.Bedrock_Blend, BLOCK_NOISE, bedrockSettings.Bedrock_Scale, bedrockSettings.Bedrock_Strength);
    });
    
    public static final ColorResolver LAVA_RESOLVER = Util.make(() -> {
        return (biome, x, z) -> blendedColor(generalSettings.Default_Color, lavaSettings.Lava_Color, (int) x, (int) z,
                lavaSettings.Lava_Blend, LIQUID_NOISE, lavaSettings.Lava_Scale, lavaSettings.Lava_Strength);
    });
    
    public static final ColorResolver NETHERRACK_RESOLVER = Util.make(() -> {
        return (biome, x, z) -> blendedColor(generalSettings.Default_Color, netherrackSettings.Netherrack_Color, (int) x, (int) z,
                netherrackSettings.Netherrack_Blend, BLOCK_NOISE, netherrackSettings.Netherrack_Scale, netherrackSettings.Netherrack_Strength);
    });
    
    public static final ColorResolver SOUL_SANDS_RESOLVER = Util.make(() -> {
        return (biome, x, z) -> blendedColor(generalSettings.Default_Color, soulsandsSettings.Soul_Sands_Color, (int) x, (int) z,
                soulsandsSettings.Soul_Sands_Blend, BLOCK_NOISE, soulsandsSettings.Soul_Sands_Scale, soulsandsSettings.Soul_Sands_Strength);
    });
    
    public static final ColorResolver ICES_RESOLVER = Util.make(() -> {
        return (biome, x, z) -> blendedColor(generalSettings.Default_Color, icesSettings.Ices_Color, (int) x, (int) z,
                icesSettings.Ices_Blend, BLOCK_NOISE, icesSettings.Ices_Scale, icesSettings.Ices_Strength);
    });
}