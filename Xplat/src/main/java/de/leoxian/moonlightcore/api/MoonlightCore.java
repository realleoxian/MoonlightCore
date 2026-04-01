package de.leoxian.moonlightcore.api;

import de.leoxian.moonlightcore.api.attachment.AttachmentType;
import de.leoxian.moonlightcore.api.datamap.DataMapType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

import static de.leoxian.moonlightcore.impl.internal.InternalMod.internalRegistryKey;
import static de.leoxian.moonlightcore.impl.internal.InternalMod.supplyRegistry;

public class MoonlightCore {

    public static final Supplier<Registry<AttachmentType<?>>> ATTACHMENT_TYPES = supplyRegistry(Registries.ATTACHMENT_TYPES);
    public static final Supplier<Registry<DataMapType<?,?>>> DATA_MAP_TYPES = supplyRegistry(Registries.DATA_MAP_TYPES);

    public static final class Registries {
        public static final ResourceKey<Registry<AttachmentType<?>>> ATTACHMENT_TYPES = internalRegistryKey("attachment_types");
        public static final ResourceKey<Registry<DataMapType<?, ?>>> DATA_MAP_TYPES = internalRegistryKey("data_map_types");

    }

}
