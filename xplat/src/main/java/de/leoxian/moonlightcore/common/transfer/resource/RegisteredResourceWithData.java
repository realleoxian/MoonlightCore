package de.leoxian.moonlightcore.common.transfer.resource;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;

public interface RegisteredResourceWithData<T> extends RegisteredResource<T>, DataComponentHolder {
    RegisteredResourceWithData<T> applyPatch(DataComponentPatch patch);

    DataComponentPatch componentsPatch();
}
