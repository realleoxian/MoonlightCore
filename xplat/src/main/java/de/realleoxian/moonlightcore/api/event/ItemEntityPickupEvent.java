package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

public sealed class ItemEntityPickupEvent extends EventBase {
    public final Player player;
    public final ItemEntity itemEntity;

    public ItemEntityPickupEvent(Player player, ItemEntity itemEntity) {
        this.player = player;
        this.itemEntity = itemEntity;
    }

    public static final class Pre extends ItemEntityPickupEvent implements CancellableEvent {
        @ApiStatus.Internal
        public Pre(Player player, ItemEntity itemEntity) {
            super(player, itemEntity);
        }
    }

    public static final class Post extends ItemEntityPickupEvent {
        public final ItemStack originalStack;

        public Post(Player player, ItemEntity itemEntity, ItemStack originalStack) {
            super(player, itemEntity);
            this.originalStack = originalStack;
        }
    }
}
