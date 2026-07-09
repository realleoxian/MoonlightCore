package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import de.leoxian.moonlightcore.common.event.base.EventResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface ItemTossEvent {
    Event<ItemTossEvent> EVENT = Event.create(ItemTossEvent.class, listeners -> (player, entity) -> {
       var result = EventResult.TRUE;
        for (final var listener : listeners) {
           result = listener.onItemToss(player, entity);
           if (result.cancelFurtherEventProcessing()) {
               break;
           }
        }
        return result;
    });

    EventResult onItemToss(Player player, ItemEntity entity);
}
