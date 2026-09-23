package de.leoxian.moonlightcore.fabric.common.mixin.accessor;

import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MappedRegistry.class)
public interface MappedRegistryAccessor {
	@Accessor
	void setFrozen(boolean frozen);
}
