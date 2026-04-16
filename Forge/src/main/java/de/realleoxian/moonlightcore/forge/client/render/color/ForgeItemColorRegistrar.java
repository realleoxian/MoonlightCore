package de.realleoxian.moonlightcore.forge.client.render.color;

import com.mojang.datafixers.util.Pair;
import de.realleoxian.moonlightcore.api.client.render.color.ItemColorRegistrar;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ForgeItemColorRegistrar implements ItemColorRegistrar {
    private final List<Pair<Supplier<ItemLike>, ItemColor>> itemColors = new ArrayList<>();

    @SubscribeEvent
    public void onRegisterBlockColor(RegisterColorHandlersEvent.Item event) {
        this.itemColors.forEach(p -> event.register(p.getSecond(), p.getFirst().get()));
    }

    @Override
    public void registerItemColor(ItemColor color, Supplier<ItemLike> item) {
        this.itemColors.add(Pair.of(item, color));
    }
}
