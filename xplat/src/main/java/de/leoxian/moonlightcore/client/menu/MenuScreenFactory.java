package de.leoxian.moonlightcore.client.menu;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@FunctionalInterface
public interface MenuScreenFactory<T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>> {
    S create(T menu, Inventory inventory, Component title);
}
