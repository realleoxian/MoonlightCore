package de.leoxian.moonlightcore.util;

import de.leoxian.moonlightcore.attachment.AttachmentType;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.registry.RegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class MoonlightRegistries {

    public static final Registry<AttachmentType<?>> ATTACHMENT_TYPE = RegistryBuilder.build(MoonlightRegistries.Keys.ATTACHMENTS_TYPE);
    public static final Registry<AttachmentType<?>> SYNCED_ATTACHMENT_TYPE = RegistryBuilder.build(Keys.SYNCED_ATTACHMENT_TYPE);

    private MoonlightRegistries() {}

    public static final class Keys {
        public static final ResourceKey<Registry<AttachmentType<?>>> ATTACHMENTS_TYPE = create("attachment_type");
        public static final ResourceKey<Registry<AttachmentType<?>>> SYNCED_ATTACHMENT_TYPE = create("synced_attachment_type");

        private static <T> ResourceKey<Registry<T>> create(String name) {
            return ResourceKey.createRegistryKey(new ResourceLocation(MoonlightCore.MOD_ID, name));
        }
    }
}
