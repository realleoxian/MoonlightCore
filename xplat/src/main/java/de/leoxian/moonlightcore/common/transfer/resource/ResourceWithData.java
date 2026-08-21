package de.leoxian.moonlightcore.common.transfer.resource;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;

public interface ResourceWithData<T> extends Resource, DataComponentHolder {
    ResourceWithData<T> applyPatch(DataComponentPatch components);

    DataComponentPatch componentsPatch();
}
