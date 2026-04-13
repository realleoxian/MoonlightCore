package de.realleoxian.moonlightcore.api;

import de.realleoxian.moonlightcore.api.attachment.AttachmentType;
import de.realleoxian.moonlightcore.api.registry.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class MoonlightCoreRegistries {
    private static boolean initialized = false;

    private static Supplier<Registry<AttachmentType<?>>> attachmentTypesRegistry;

    public static void setup(RegistryHelper registryHelper) {
        if (initialized) return;
        initialized = true;

        attachmentTypesRegistry = registryHelper.getRegistry(Keys.ATTACHMENT_TYPE);
    }

    public static Registry<AttachmentType<?>> attachmentTypes() {
        return attachmentTypesRegistry.get();
    }

    public static final class Keys {
        public static final ResourceKey<Registry<AttachmentType<?>>> ATTACHMENT_TYPE = ResourceKey.createRegistryKey(new ResourceLocation("moonlightcore", "attachment_type"));

        private Keys() {}
    }
}
