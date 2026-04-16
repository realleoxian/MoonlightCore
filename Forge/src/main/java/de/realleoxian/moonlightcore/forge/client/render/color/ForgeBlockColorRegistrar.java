package de.realleoxian.moonlightcore.forge.client.render.color;

import com.mojang.datafixers.util.Pair;
import de.realleoxian.moonlightcore.api.client.render.color.BlockColorRegistrar;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ForgeBlockColorRegistrar implements BlockColorRegistrar {
    private final List<Pair<Supplier<Block>, BlockColor>> blockColors = new ArrayList<>();

    @SubscribeEvent
    public void onRegisterBlockColor(RegisterColorHandlersEvent.Block event) {
        this.blockColors.forEach(p -> event.register(p.getSecond(), p.getFirst().get()));
    }

    @Override
    public void registerBlockColor(BlockColor color, Supplier<Block> block) {
        this.blockColors.add(Pair.of(block, color));
    }
}
