package de.leoxian.moonlightcore.neoforge.client.menu;

import de.leoxian.moonlightcore.client.menu.MenuScreenFactory;
import de.leoxian.moonlightcore.client.menu.MenuScreenRegistrar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.function.Supplier;

public record NeoforgeMenuScreenRegistrar(RegisterMenuScreensEvent event) implements MenuScreenRegistrar {
    @Override
    public <T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void register(Supplier<MenuType<T>> menuType, MenuScreenFactory<T, S> factory) {
        event.register(menuType.get(), factory::create);
    }
}
