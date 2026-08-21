package de.leoxian.moonlightcore.common.transfer.capability;

import de.leoxian.moonlightcore.common.capability.block.BlockCapability;
import de.leoxian.moonlightcore.common.capability.entity.EntityCapability;
import de.leoxian.moonlightcore.common.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.common.transfer.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public final class FluidStorage {
    public static final BlockCapability<Storage<FluidResource>, Direction> BLOCK = BlockCapability.get(
            Identifier.fromNamespaceAndPath("moonlightcore", "fluid_sided_storage"),
            Storage.asClass(),
            Direction.class);
    public static final EntityCapability<Storage<FluidResource>, Void> ENTITY = EntityCapability.get(
            Identifier.fromNamespaceAndPath("moonlightcore", "fluid_entity_storage"),
            Storage.asClass(),
            Void.class
    );

    private FluidStorage() {}
}
