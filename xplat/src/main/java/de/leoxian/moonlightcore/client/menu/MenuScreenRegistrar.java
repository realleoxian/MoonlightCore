package de.leoxian.moonlightcore.client.menu;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface MenuScreenRegistrar {
    static void init(String namespace, Consumer<MenuScreenRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.menuScreens(namespace, initializer);
    }

    <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void register(Supplier<MenuType<T>> menuType, MenuScreenFactory<T, S> factory);
}
