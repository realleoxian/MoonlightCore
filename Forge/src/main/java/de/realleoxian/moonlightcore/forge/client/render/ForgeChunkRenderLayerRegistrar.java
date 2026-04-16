package de.realleoxian.moonlightcore.forge.client.render;

import com.mojang.datafixers.util.Pair;
import de.realleoxian.moonlightcore.api.client.render.ChunkRenderLayerRegistrar;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ForgeChunkRenderLayerRegistrar implements ChunkRenderLayerRegistrar {
    private final List<Pair<Supplier<Block>, RenderType>> blockRenderTypes = new ArrayList<>();
    private final List<Pair<Supplier<Fluid>, RenderType>> fluidRenderTypes = new ArrayList<>();

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        this.blockRenderTypes.forEach(p -> ItemBlockRenderTypes.setRenderLayer(p.getFirst().get(), p.getSecond()));
        this.fluidRenderTypes.forEach(p -> ItemBlockRenderTypes.setRenderLayer(p.getFirst().get(), p.getSecond()));
    }

    @Override
    public void registerBlock(RenderType renderType, Supplier<Block> block) {
        this.blockRenderTypes.add(Pair.of(block, renderType));
    }

    @Override
    public void registerFluid(RenderType renderType, Supplier<Fluid> fluid) {
        this.fluidRenderTypes.add(Pair.of(fluid, renderType));
    }
}
