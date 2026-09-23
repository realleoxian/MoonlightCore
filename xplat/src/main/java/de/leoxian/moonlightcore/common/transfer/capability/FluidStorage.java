package de.leoxian.moonlightcore.common.transfer.capability;

import de.leoxian.moonlightcore.common.capability.block.BlockCapability;
import de.leoxian.moonlightcore.common.capability.entity.EntityCapability;
import de.leoxian.moonlightcore.common.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.common.transfer.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public final class FluidStorage {
	public static final BlockCapability<Storage<FluidResource>, Direction> BLOCK = BlockCapability.create(
			Identifier.fromNamespaceAndPath("moonlightcore", "fluid_sided_storage"),
			Storage.asClass(),
			Direction.class);
	public static final EntityCapability<Storage<FluidResource>, Void> ENTITY = EntityCapability.create(
			Identifier.fromNamespaceAndPath("moonlightcore", "fluid_entity_storage"),
			Storage.asClass(),
			Void.class
	);

	static {
		BLOCK.registerFallback((_, _, _, blockEntity, context) -> {
			if (blockEntity instanceof SidedStorageBlockEntity sidedStorageBlockEntity) {
				return sidedStorageBlockEntity.getFluidStorage(context);
			}
			return null;
		});
	}

	private FluidStorage() {}
}
