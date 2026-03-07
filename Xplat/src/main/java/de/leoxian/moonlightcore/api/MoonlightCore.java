package de.leoxian.moonlightcore.api;

import de.leoxian.moonlightcore.api.attachment.AttachmentType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

import static de.leoxian.moonlightcore.impl.internal.ModInternal.internalRegistryKey;
import static de.leoxian.moonlightcore.impl.internal.ModInternal.supplyRegistry;

public class MoonlightCore {
    public static final Supplier<Registry<AttachmentType<?>>> ATTACHMENT_TYPES = supplyRegistry(MoonlightCore.Registries.ATTACHMENT_TYPES);

    public static final class Registries {
        public static final ResourceKey<Registry<AttachmentType<?>>> ATTACHMENT_TYPES = internalRegistryKey("attachment_types");
    }
}
