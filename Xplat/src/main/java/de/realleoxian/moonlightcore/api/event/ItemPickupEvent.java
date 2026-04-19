package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public interface ItemPickupEvent {
    EventBus<ItemPickupEvent> EVENT = EventBus.create(ItemPickupEvent.class, (listeners) -> (player, item) -> {
        for(ItemPickupEvent listener : listeners) {
            EventResult result = listener.onItemPickup(player, item);

            if (result.cancelFurtherProcessing) {
                return result;
            }
        }

        return EventResult.TRUE;
    });

    EventResult onItemPickup(Player player, ItemEntity item);
}
