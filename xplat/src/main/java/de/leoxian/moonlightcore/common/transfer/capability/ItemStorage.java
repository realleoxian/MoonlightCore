package de.leoxian.moonlightcore.common.transfer.capability;

import de.leoxian.moonlightcore.common.capability.block.BlockCapability;
import de.leoxian.moonlightcore.common.capability.entity.EntityCapability;
import de.leoxian.moonlightcore.common.transfer.item.ItemResource;
import de.leoxian.moonlightcore.common.transfer.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public final class ItemStorage {
    public static final BlockCapability<Storage<ItemResource>, Direction> BLOCK = BlockCapability.get(
            Identifier.fromNamespaceAndPath("moonlightcore", "item_sided_storage"),
            Storage.asClass(),
            Direction.class);
    public static final EntityCapability<Storage<ItemResource>, Void> ENTITY = EntityCapability.get(
            Identifier.fromNamespaceAndPath("moonlightcore", "item_entity_storage"),
            Storage.asClass(),
            Void.class
    );

    private ItemStorage() {}
}
