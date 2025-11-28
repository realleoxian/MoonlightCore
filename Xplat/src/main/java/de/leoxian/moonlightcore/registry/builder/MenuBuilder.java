/*
 * This source code file is subject to the terms of the Mozilla Public License, v. 2.0.
 * Based on code from Tterrag1098's Registrate (https://github.com/tterrag1098/Registrate).
 * Modifications by Leoxian, 2025
 */
package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.platform.EnvironmentSide;
import de.leoxian.moonlightcore.registry.DeferredRegistrar;
import de.leoxian.moonlightcore.util.nullness.NonnullSupplier;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class MenuBuilder<T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> extends AbstractBuilder<MenuType<?>, MenuType<T>, MenuBuilder<T, S>> {

    public static <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> MenuBuilder<T, S> builder(DeferredRegistrar<MenuType<?>> registrar, String name, MenuFactory<T> menuFactory, NonnullSupplier<ScreenFactory<T, S>> screenFactory) {
        return new MenuBuilder<>(registrar, name, menuFactory, screenFactory);
    }

    private final MenuFactory<T> menuFactory;
    private final NonnullSupplier<ScreenFactory<T, S>> screenFactory;

    protected MenuBuilder(DeferredRegistrar<MenuType<?>> registrar, String name, MenuFactory<T> menuFactory, NonnullSupplier<ScreenFactory<T, S>> screenFactory) {
        super(registrar, name);
        this.menuFactory = menuFactory;
        this.screenFactory = screenFactory;
    }

    @Override
    protected MenuType<T> buildEntry() {
        MenuType<T> menuType = new MenuType<>((containerId, inventory) -> this.menuFactory.create(getValue(), containerId, inventory), FeatureFlags.DEFAULT_FLAGS);
        EnvironmentSide.CLIENT.runIfCurrent(() -> () -> {
            ScreenFactory<T, S> screenFactory = this.screenFactory.get();
            MenuScreens.register(menuType, screenFactory::create);
        });

        return menuType;
    }

    public interface MenuFactory<T extends AbstractContainerMenu> {
        T create(MenuType<T> type, int containerId, Inventory inventory);
    }

    public interface ScreenFactory<M extends AbstractContainerMenu, T extends Screen & MenuAccess<M>> {
        T create(M menu, Inventory inventory, Component displayName);
    }

}
