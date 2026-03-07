package de.leoxian.moonlightcore.api.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public interface ItemPickupEvent {
    /**
     * @see ItemPickupEvent#onItemPickup(Player, ItemEntity)
     */
    EventBus<ItemPickupEvent> EVENT = EventBus.create((listeners) -> (player, item) -> {
        for(ItemPickupEvent listener : listeners) {
            if(listener.onItemPickup(player, item).isFalse()) {
                return EventResult.FALSE;
            }
        }

        return EventResult.TRUE;
    });

    /**
     * Invoked just before a player tries to pickup an {@link ItemEntity}
     * @param player    The player
     * @param item      The {@link ItemEntity} the player tries to pickup
     * @return {@code true} or {@code false} determining if the player can pick up the item or not
     */
    EventResult onItemPickup(Player player, ItemEntity item);

}
