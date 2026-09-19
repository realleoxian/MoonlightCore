package de.leoxian.moonlightcore.fabric.client.pack;

import de.leoxian.moonlightcore.client.pack.ClientResourceReloadListenerRegistrar;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public enum FabricClientResourceReloadListenerRegistrar implements ClientResourceReloadListenerRegistrar {
    INSTANCE
    ;

    @Override
    public void register(Identifier id, PreparableReloadListener listener) {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(id, listener);
    }

    @Override
    public void addDependency(Identifier first, Identifier second) {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).addListenerOrdering(first, second);
    }
}
