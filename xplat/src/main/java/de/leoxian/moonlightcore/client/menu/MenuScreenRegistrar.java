package de.leoxian.moonlightcore.client.menu;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface MenuScreenRegistrar {
	/// Registers a new registrar to the given namespace
	/// @param namespace The mod's id to add this registrar to
	/// @param initializer The initializer of the registrar
	static void configure(String namespace, Consumer<MenuScreenRegistrar> initializer) {
		XplatClientAbstraction.INSTANCE.menuScreens(namespace, initializer);
	}

	/// Register a screen to the given menu
	/// @param menuType The menu type
	/// @param factory The screen factory for the menu's screen
	/// @param <T> The container menu type
	/// @param <S> The screen with access to the menu
	<T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> void register(Supplier<MenuType<T>> menuType, MenuScreenFactory<T, S> factory);
}
