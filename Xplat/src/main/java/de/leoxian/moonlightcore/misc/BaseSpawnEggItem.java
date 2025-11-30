package de.leoxian.moonlightcore.misc;

import de.leoxian.moonlightcore.event.client.RenderingEvents;
import de.leoxian.moonlightcore.event.common.CommonLifecycleEvent;
import de.leoxian.moonlightcore.platform.EnvironmentSide;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BaseSpawnEggItem extends SpawnEggItem {
    private static final List<BaseSpawnEggItem> MODDED_EGGS = new ArrayList<>();
    private static final Map<EntityType<? extends Mob>, BaseSpawnEggItem> TYPE_MAP = new IdentityHashMap<>();

    private static final net.minecraft.core.dispenser.DispenseItemBehavior DEFAULT_DISPENSER_BEHAVIOR = (source, stack) -> {
        Direction face = source.getBlockState().getValue(DispenserBlock.FACING);
        EntityType<?> type = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());

        try {
            type.spawn(source.getLevel(), stack, null, source.getPos().relative(face), MobSpawnType.DISPENSER, face != Direction.UP, false);
        } catch (Exception exception) {
            DispenseItemBehavior.LOGGER.error("Error while dispensing spawn egg from dispenser at {}", source.getPos(), exception);
            return ItemStack.EMPTY;
        }

        stack.shrink(1);
        source.getLevel().gameEvent(GameEvent.ENTITY_PLACE, source.getPos(), GameEvent.Context.of(source.getBlockState()));
        return stack;
    };

    @ApiStatus.Internal
    public static void setupSpawnEggs() {
        CommonLifecycleEvent.SETUP.subscribe(() -> {
            MODDED_EGGS.forEach(egg -> {
                DispenseItemBehavior dispenseBehavior = egg.createDispenseBehavior();

                if(dispenseBehavior != null) {
                    DispenserBlock.registerBehavior(egg, dispenseBehavior);
                }

                TYPE_MAP.put(egg.typeSupplier.get(), egg);
            });
        });

        EnvironmentSide.CLIENT.runIfCurrent(() -> () ->
                RenderingEvents.ITEM_COLOR_REGISTRATION.subscribe(output ->
                        MODDED_EGGS.forEach(egg ->
                                output.accept((stack, idx) -> egg.getColor(idx), egg))));
    }

    public static @Nullable SpawnEggItem byId(@Nullable EntityType<?> type) {
        return TYPE_MAP.get(type);
    }

    private final Supplier<? extends EntityType<? extends Mob>> typeSupplier;

    public BaseSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties properties) {
        super(null, backgroundColor, highlightColor, properties);
        this.typeSupplier = type;

        MODDED_EGGS.add(this);
    }

    protected DispenseItemBehavior createDispenseBehavior() {
        return DEFAULT_DISPENSER_BEHAVIOR;
    }
}
