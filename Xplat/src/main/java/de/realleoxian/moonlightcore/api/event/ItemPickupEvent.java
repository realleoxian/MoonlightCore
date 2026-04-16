package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public interface ItemPickupEvent {
    EventBus<ItemPickupEvent> EVENT = EventBus.create(ItemPickupEvent.class, (listeners) -> (player, item) -> {
        for(ItemPickupEvent listener : listeners) {
            if(listener.onItemPickup(player, item).isFalse()) {
                return EventResult.FALSE;
            }
        }

        return EventResult.TRUE;
    });

    EventResult onItemPickup(Player player, ItemEntity item);
}
