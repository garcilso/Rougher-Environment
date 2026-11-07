package com.garcilso.rougher_environment;

import com.garcilso.rougher_environment.config.Rougher_Environment_Config;
import com.garcilso.rougher_environment.mixin.BiomeColorsAccessor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Math.clamp;

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
    public static final int NOISE_OCTAVES = 2;
    public static final int NOISE_OCTAVES_MORE = 3;
    public static final List<Integer> OCTAVES = IntStream.rangeClosed(0, NOISE_OCTAVES).boxed().toList();
    public static final List<Integer> OCTAVES_MORE = IntStream.rangeClosed(0, NOISE_OCTAVES_MORE).boxed().toList();
    public static final PerlinSimplexNoise FOLIAGE_NOISE = new PerlinSimplexNoise(new XoroshiroRandomSource("FOLIAGE_NOISE".hashCode()), OCTAVES);
    public static final PerlinSimplexNoise DRY_FOLIAGE_NOISE = new PerlinSimplexNoise(new XoroshiroRandomSource("DRY_FOLIAGE_NOISE".hashCode()), OCTAVES);
    
    public static final PerlinSimplexNoise BLOCK_NOISE = new PerlinSimplexNoise(new XoroshiroRandomSource("BLOCK_NOISE".hashCode()), OCTAVES);
    public static final PerlinSimplexNoise LIQUID_NOISE = new PerlinSimplexNoise(new XoroshiroRandomSource("LIQUID_NOISE".hashCode()), OCTAVES_MORE);
    
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
    
    private static int modifyColour(PerlinSimplexNoise generator, ColorResolver resolver, Biome biome, double x, double z, double scale, double darkness) {
        
        final int base = resolver.getColor(biome, x, z);
        double value = generator.getValue(x / scale, z / scale, false);
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
    
    private static int blendedColor(int color, int colorBlend, int x, int z, int distance, PerlinSimplexNoise Noise, float scale, float darkness) {
        int length = distance*distance;
        
        float A = getAlpha(color); float R = getRed(color); float G = getGreen(color); float B = getBlue(color);
        
        int CornerX = x - (distance/2); int CornerZ = z - (distance/2);
        
        for (int Blockx = 0; Blockx < distance; Blockx++) {
            for (int Blockz = 0; Blockz < distance; Blockz++) {
                int BlockColor = modifyColourNoBiome(Noise, color, colorBlend, CornerX+Blockx, CornerZ+Blockz, scale, darkness);
                
                A += getAlpha(BlockColor); R += getRed(BlockColor); G += getGreen(BlockColor); B += getBlue(BlockColor);
            }
        }
        
        int minColor = blend(color, colorBlend, (float) darkness);
        int finalColor = clamp(toInt(new float[] {A/length, R/length, G/length, B/length}), minColor, color);
        
        if (finalColor == minColor) {finalColor = color;} //this code sucks ass the first time it worked well but i forgot to save it and now its this
        
        return finalColor;
    }
    
    private static int modifyColourNoBiome(PerlinSimplexNoise generator, int color, int colorBlend, double x, double z, double scale, double darkness) {
        
        double value = generator.getValue(x / scale, z / scale, false);
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
}