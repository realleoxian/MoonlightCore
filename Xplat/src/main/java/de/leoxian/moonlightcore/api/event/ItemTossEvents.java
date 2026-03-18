package de.leoxian.moonlightcore.api.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ItemTossEvents {
    /**
     * @see Pre#onPreItemToss(Player, ItemStack)
     */
    public static final EventBus<ItemTossEvents.Pre> PRE_ITEM_TOSS = EventBus.create((listeners) -> (player, stack) -> {
        for(ItemTossEvents.Pre listener : listeners) {
            EventResult result = listener.onPreItemToss(player, stack);

            if(result.cancelFurtherProcessing) {
                return result;
            }
        }

        return EventResult.TRUE;
    });
    /**
     * @see Post#onItemToss(Player, ItemEntity)
     */
    public static final EventBus<ItemTossEvents.Post> POST_ITEM_TOSS = EventBus.create((listeners) -> (player, item) -> {
       for(ItemTossEvents.Post listener : listeners) {
           listener.onItemToss(player, item);
       }
    });

    private ItemTossEvents() {}

    public interface Pre {
        /**
         * Invoked just before a player tries to toss(drop) an item
         * @param player    The player
         * @param stack     The stack that may get toss
         * @return If the event was cancelled or not. If cancelled, the player will not toss the item and {@link #POST_ITEM_TOSS post} will not be invoked
         */
        EventResult onPreItemToss(Player player, ItemStack stack);
    }

    public interface Post {
        /**
         * Invoked when a player tosses/drops an item
         * @param player    The player
         * @param item      The {@link ItemEntity} that was tossed/dropped
         */
        void onItemToss(Player player, ItemEntity item);
    }
}
