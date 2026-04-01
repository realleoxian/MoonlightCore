package de.leoxian.moonlightcore.api.transfer.item;

import com.google.common.collect.MapMaker;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public final class CursorSlotWrapper extends SingleStackStorage {
    private static final Map<AbstractContainerMenu, CursorSlotWrapper> WRAPPERS = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();

    public static CursorSlotWrapper get(AbstractContainerMenu containerMenu) {
        return WRAPPERS.computeIfAbsent(containerMenu, CursorSlotWrapper::new);
    }

    private final AbstractContainerMenu containerMenu;

    private CursorSlotWrapper(AbstractContainerMenu containerMenu) {
        this.containerMenu = containerMenu;
    }

    @Override
    public void setStack(ItemStack stack) {
        containerMenu.setCarried(stack);
    }

    @Override
    public ItemStack getStack() {
        return containerMenu.getCarried();
    }
}
