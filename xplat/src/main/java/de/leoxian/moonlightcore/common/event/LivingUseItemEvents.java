package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.CompoundEventResult;
import de.leoxian.moonlightcore.common.event.base.Event;
import de.leoxian.moonlightcore.common.event.base.EventResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class LivingUseItemEvents {
    public static final Event<Start> START = Event.create(Start.class, listeners -> (entity, stack, usedHand, durationTicks) -> {
       var result = EventResult.SUCCESS;
       for (final var listener : listeners) {
           result = listener.onItemUseStart(entity, stack, usedHand, durationTicks);
           if (result.cancelFurtherEventProcessing()) {
               break;
           }
       }

       return result;
    });
    public static final Event<Tick> TICK = Event.create(Tick.class, listeners -> (entity, stack, usedHand, durationTicks) -> {
       var result = EventResult.SUCCESS;
       for (final var listener : listeners) {
           result = listener.onItemUseTick(entity, stack, usedHand, durationTicks);
           if (result.cancelFurtherEventProcessing()) {
               break;
           }
       }
       return result;
    });
    public static final Event<Stop> STOP = Event.create(Stop.class, listeners -> (entity, stack, usedHand, durationTicks) -> {
        var result = EventResult.SUCCESS;
        for (final var listener : listeners) {
            result = listener.onItemUseStop(entity, stack, usedHand, durationTicks);
            if (result.cancelFurtherEventProcessing()) {
                break;
            }
        }
        return result;
    });
    public static final Event<Finish> FINISH = Event.create(Finish.class, listeners -> (entity, stack, duration) -> {
       var result = CompoundEventResult.<ItemStack>pass();
       for (final var listener : listeners) {
           result = listener.onItemUseFinish(entity, stack, duration);
           if (result.result().cancelFurtherEventProcessing()) {
               break;
           }
       }
       return result;
    });

    private LivingUseItemEvents() {}

    @FunctionalInterface
    public interface Start {
        EventResult onItemUseStart(LivingEntity entity, ItemStack stack, InteractionHand usedHand, int durationTicks);
    }

    @FunctionalInterface
    public interface Tick {
        EventResult onItemUseTick(LivingEntity entity, ItemStack stack, InteractionHand usedHand, int durationTicks);
    }

    @FunctionalInterface
    public interface Stop {
        EventResult onItemUseStop(LivingEntity entity, ItemStack stack, InteractionHand usedHand, int durationTicks);
    }

    @FunctionalInterface
    public interface Finish {
        CompoundEventResult<ItemStack> onItemUseFinish(LivingEntity entity, ItemStack stack, int duration);
    }
}
