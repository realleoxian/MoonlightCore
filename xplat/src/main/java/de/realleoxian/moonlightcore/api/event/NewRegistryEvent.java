package de.realleoxian.moonlightcore.api.event;

import de.realleoxian.moonlightcore.api.registry.RegistryInformation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

public final class NewRegistryEvent extends EventBase {
    public static final Event<NewRegistryEvent> EVENT = Event.create(NewRegistryEvent.class);

    private final Consumer<RegistryInformation> registerFunc;

    @ApiStatus.Internal
    public NewRegistryEvent(Consumer<RegistryInformation> registerFunc) {
        this.registerFunc = registerFunc;
    }

    public void register(RegistryInformation information) {
        this.registerFunc.accept(information);
    }
}
