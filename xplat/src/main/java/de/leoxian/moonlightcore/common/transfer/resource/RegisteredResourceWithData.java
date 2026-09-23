package de.leoxian.moonlightcore.common.transfer.resource;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentPatch;

public interface RegisteredResourceWithData<T> extends RegisteredResource<T>, DataComponentHolder {
	RegisteredResourceWithData<T> applyPatch(DataComponentPatch patch);

	DataComponentPatch componentsPatch();
}
