package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import de.leoxian.moonlightcore.common.event.base.EventResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public final class ItemEntityPickupEvents {
    public static final Event<Pre> PRE = Event.create(Pre.class, listeners -> (player, itemEntity) -> {
       var result = EventResult.TRUE;
       for (final var listener : listeners) {
           result = listener.onPreItemEntityPickup(player, itemEntity);
           if (result.cancelFurtherEventProcessing()) {
               break;
           }
       }
       return result;
    });
    public static final Event<Post> POST = Event.create(Post.class, listeners -> (player, entity) -> {
       for (final var listener : listeners) {
           listener.onPostItemEntityPickup(player, entity);
       }
    });

    private ItemEntityPickupEvents() {}

    @FunctionalInterface
    public interface Pre {
        EventResult onPreItemEntityPickup(Player player, ItemEntity itemEntity);
    }

    @FunctionalInterface
    public interface Post {
        void onPostItemEntityPickup(Player player, ItemEntity entity);
    }
}
