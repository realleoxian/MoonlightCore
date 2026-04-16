package de.realleoxian.moonlightcore.forge.client.render;

import com.mojang.datafixers.util.Pair;
import de.realleoxian.moonlightcore.api.client.render.BlockEntityRendererRegistrar;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ForgeBlockEntityRendererRegistrar implements BlockEntityRendererRegistrar {
    private final List<Pair<Supplier<BlockEntityType<?>>, BlockEntityRendererProvider<BlockEntity>>> providers = new ArrayList<>();

    @SubscribeEvent
    public void onRegisterBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers entityRenderersEvent) {
        this.providers.forEach((p) -> entityRenderersEvent.registerBlockEntityRenderer(p.getFirst().get(), p.getSecond()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <BE extends BlockEntity> void register(Supplier<BlockEntityType<BE>> blockEntityType, BlockEntityRendererProvider<BE> provider) {
        this.providers.add(Pair.of(blockEntityType::get, (BlockEntityRendererProvider<BlockEntity>) provider));
    }
}
