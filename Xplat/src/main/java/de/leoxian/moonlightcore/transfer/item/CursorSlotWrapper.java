package de.leoxian.moonlightcore.transfer.item;

import com.google.common.collect.MapMaker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class CursorSlotWrapper extends SingleStackStorage {
    private static final Map<AbstractContainerMenu, CursorSlotWrapper> WRAPPERS = new MapMaker().weakValues().makeMap();

    public static CursorSlotWrapper get(AbstractContainerMenu containerMenu) {
        return WRAPPERS.computeIfAbsent(containerMenu, CursorSlotWrapper::new);
    }

    private final AbstractContainerMenu containerMenu;

    private CursorSlotWrapper(AbstractContainerMenu containerMenu) {
        this.containerMenu = containerMenu;
    }

    @Override
    public void setStack(ItemStack stack) {
        this.containerMenu.setCarried(stack);
    }

    @Override
    public ItemStack getStack() {
        return this.containerMenu.getCarried();
    }

    @Override
    public String toString() {
        return "CursorSlotWrapper[" + this.containerMenu + "/" + BuiltInRegistries.MENU.getKey(this.containerMenu.getType()) + "]";
    }
}
