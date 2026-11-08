package com.garcilso.rougher_environment;

import com.garcilso.rougher_environment.config.Rougher_Environment_Config;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.mojang.text2speech.Narrator.LOGGER;

@Environment(EnvType.CLIENT)
public class Rougher_Environment implements ClientModInitializer {
    private static final String id = "rougher_environment";
    
    private static ConfigHolder<Rougher_Environment_Config> configHolder;
    
    public static Rougher_Environment_Config getConfig() {
        return configHolder.getConfig();
    }
    
    public static void saveConfig() {
        configHolder.save();
    }
    
    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer(id).ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier.parse(id), container,
                    "Rougher Environment Resources",
                    ResourcePackActivationType.NORMAL
            );
        });
        
        loadConfigFile(); //w Gamma Utils thanks for making ts
        
        final Rougher_Environment_Config.GeneralSettings generalSettings = Rougher_Environment.getConfig().generalSettings;
        
        BlockColorRegistry.register(List.of(new BlockTintSource() {
            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return ARGB.opaque(Rougher_Environment_Common.SAND_RESOLVER.getColor(null, pos.getX(), pos.getZ())
                );
            }
            
            @Override
            public int color(BlockState state) {
                return ARGB.opaque(generalSettings.Default_Color);
            }
        }), Blocks.SAND);
        
        BlockColorRegistry.register(List.of(new BlockTintSource() {
            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return ARGB.opaque(Rougher_Environment_Common.RED_SAND_RESOLVER.getColor(null, pos.getX(), pos.getZ())
                );
            }
            
            @Override
            public int color(BlockState state) {
                return ARGB.opaque(generalSettings.Default_Color);
            }
        }), Blocks.RED_SAND);
        
        BlockColorRegistry.register(List.of(new BlockTintSource() {
            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return ARGB.opaque(Rougher_Environment_Common.END_STONE_RESOLVER.getColor(null, pos.getX(), pos.getZ())
                );
            }
            
            @Override
            public int color(BlockState state) {
                return ARGB.opaque(generalSettings.Default_Color);
            }
        }), Blocks.END_STONE);
        
        BlockColorRegistry.register(List.of(new BlockTintSource() {
            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return ARGB.opaque(Rougher_Environment_Common.BEDROCK_RESOLVER.getColor(null, pos.getX(), pos.getZ())
                );
            }
            
            @Override
            public int color(BlockState state) {
                return ARGB.opaque(generalSettings.Default_Color);
            }
        }), Blocks.BEDROCK);
        
        BlockColorRegistry.register(List.of(new BlockTintSource() {
            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return ARGB.opaque(Rougher_Environment_Common.NETHERRACK_RESOLVER.getColor(null, pos.getX(), pos.getZ())
                );
            }
            
            @Override
            public int color(BlockState state) {
                return ARGB.opaque(generalSettings.Default_Color);
            }
        }), Blocks.NETHERRACK);
        
        BlockColorRegistry.register(List.of(new BlockTintSource() {
            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return ARGB.opaque(Rougher_Environment_Common.SOUL_SANDS_RESOLVER.getColor(null, pos.getX(), pos.getZ())
                );
            }
            
            @Override
            public int color(BlockState state) {
                return ARGB.opaque(generalSettings.Default_Color);
            }
        }), Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
        
        BlockColorRegistry.register(List.of(new BlockTintSource() {
            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return ARGB.opaque(Rougher_Environment_Common.ICES_RESOLVER.getColor(null, pos.getX(), pos.getZ())
                );
            }
            
            @Override
            public int color(BlockState state) {
                return ARGB.opaque(generalSettings.Default_Color);
            }
        }), Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE);
        
        FluidRenderingRegistry.register(
                Fluids.LAVA,
                Fluids.FLOWING_LAVA,
                new FluidModel.Unbaked(
                        new Material(Identifier.withDefaultNamespace("block/lava_still")),
                        new Material(Identifier.withDefaultNamespace("block/lava_flow")),
                        new Material(Identifier.withDefaultNamespace("block/water_overlay")),
                        new BlockTintSource() {
                            @Override
                            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                                return ARGB.opaque(Rougher_Environment_Common.LAVA_RESOLVER.getColor(null, pos.getX(), pos.getZ())
                                );
                            }
                            
                            @Override
                            public int color(BlockState state) {
                                return ARGB.opaque(generalSettings.Default_Color);
                            }
                        }
                )
        );
    }
    
    private static void loadConfigFile() {
        try {
            precheckConfigFile();
            configHolder = AutoConfig.register(Rougher_Environment_Config.class, GsonConfigSerializer::new);
        }
        catch (Exception e) {
            // Extra message to help the user in case the precheck didn't work.
            LOGGER.error("Failed to load rougher_environment config file, try manually deleting the file to resolve this issue. Report this issue on the Gamma Utils repository if the game continues to crash.");
            throw e;
        }
        
        configHolder.registerSaveListener((manager, data) -> {
            Minecraft.getInstance().reloadResourcePacks();
            return InteractionResult.SUCCESS;
        });
    }
    
    // In rare cases the config file might be completely empty, which causes Cloth Config to crash. This should resolve that issue.
    private static void precheckConfigFile() {
        Path configFolder = FabricLoader.getInstance().getConfigDir();
        Path configFile = configFolder.resolve("rougher_environment.json");
        
        try {
            if (Files.exists(configFile) && Files.size(configFile) == 0) {
                LOGGER.warn("Config file is empty, deleting it so a new one can be created by Cloth Config");
                Files.delete(configFile);
            }
        } catch (IOException _) {
            LOGGER.warn("Failed precheck of config file");
        }
    }
    
}