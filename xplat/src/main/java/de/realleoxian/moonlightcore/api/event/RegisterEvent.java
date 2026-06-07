package de.realleoxian.moonlightcore.api.event;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

public final class RegisterEvent extends EventBase {
    public static final Event<RegisterEvent> EVENT = Event.create(RegisterEvent.class);

    public final ResourceKey<? extends Registry<?>> registryKey;
    private final BiConsumer<ResourceLocation, ?> registerFunc;

    @ApiStatus.Internal
    public RegisterEvent(ResourceKey<? extends Registry<?>> registryKey, BiConsumer<ResourceLocation, ?> registerFunc) {
        this.registryKey = registryKey;
        this.registerFunc = registerFunc;
    }

    @SuppressWarnings("unchecked")
    public <T> T register(ResourceLocation location, T value) {
        ((BiConsumer<ResourceLocation, T>) this.registerFunc).accept(location, value);
        return value;
    }
}
