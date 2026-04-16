package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ItemTossEvents {
    public static final EventBus<ItemTossEvents.Pre> PRE_ITEM_TOSS = EventBus.create(Pre.class, (listeners) -> (player, stack) -> {
        for(ItemTossEvents.Pre listener : listeners) {
            EventResult result = listener.onPreItemToss(player, stack);

            if(result.cancelFurtherProcessing) {
                return result;
            }
        }

        return EventResult.TRUE;
    });
    public static final EventBus<ItemTossEvents.Post> POST_ITEM_TOSS = EventBus.create(Post.class, (listeners) -> (player, item) -> {
       for(ItemTossEvents.Post listener : listeners) {
           listener.onItemToss(player, item);
       }
    });

    private ItemTossEvents() {}

    public interface Pre {
        EventResult onPreItemToss(Player player, ItemEntity itemEntity);
    }

    public interface Post {
        void onItemToss(Player player, ItemEntity itemEntity);
    }
}
