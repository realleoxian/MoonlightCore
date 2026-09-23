package de.leoxian.moonlightcore.common.transfer.resource;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentPatch;

public interface ResourceWithData<T> extends Resource, DataComponentHolder {
	ResourceWithData<T> applyPatch(DataComponentPatch components);

	DataComponentPatch componentsPatch();
}
