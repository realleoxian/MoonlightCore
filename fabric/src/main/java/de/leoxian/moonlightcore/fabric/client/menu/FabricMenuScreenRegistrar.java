package de.leoxian.moonlightcore.fabric.client.menu;

import de.leoxian.moonlightcore.client.menu.MenuScreenFactory;
import de.leoxian.moonlightcore.client.menu.MenuScreenRegistrar;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public enum FabricMenuScreenRegistrar implements MenuScreenRegistrar {
    INSTANCE
    ;

    @Override
    public <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void register(Supplier<MenuType<T>> menuType, MenuScreenFactory<T, S> factory) {
        MenuScreens.register(menuType.get(), factory::create);
    }
}
