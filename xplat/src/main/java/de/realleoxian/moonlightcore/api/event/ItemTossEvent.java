package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public class ItemTossEvent extends EventBase implements CancellableEvent {
    public final ItemEntity itemEntity;
    public final Player player;

    public ItemTossEvent(ItemEntity itemEntity, Player player) {
        this.itemEntity = itemEntity;
        this.player = player;
    }
}
