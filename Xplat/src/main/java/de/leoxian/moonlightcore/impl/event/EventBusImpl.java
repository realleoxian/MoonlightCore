package de.leoxian.moonlightcore.impl.event;

import de.leoxian.moonlightcore.api.event.EventBus;
import de.leoxian.moonlightcore.api.event.EventPriority;
import de.leoxian.moonlightcore.impl.internal.InternalMod;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public final class EventBusImpl<T> implements EventBus<T> {
    public static final ResourceLocation DEFAULT_PHASE = new ResourceLocation(InternalMod.MOD_ID, "default");

    public static <T> EventBus<T> create(Function<T[], T> factory) {
        return new EventBusImpl<>(factory);
    }

    private final Object lock = new Object();

    private final Map<ResourceLocation, Phase> phases = new HashMap<>();
    private final List<Phase> sortedPhases = new ArrayList<>();

    private final Function<T[], T> invokerFactory;
    private T invoker = null;

    private EventBusImpl(Function<T[], T> invokerFactory) {
        this.invokerFactory = invokerFactory;
    }

    @Override
    public T invoker() {
        T ret = invoker;
        if(ret == null) {
            ret = invoker = buildInvoker();
        }

        return ret;
    }

    @Override
    public EventBus<T> definePhaseOrdering(ResourceLocation previous, ResourceLocation phase) {
        Objects.requireNonNull(previous, "Tried to define event phase ordering to 'null' phase");
        Objects.requireNonNull(phase, "Tried to define event phase ordering to 'null' phase");

        synchronized (lock) {
            Phase first = getOrCreatePhase(previous, false);
            Phase second = getOrCreatePhase(phase, true);
            Phase.link(first, second);

            NodeSorting.sort(sortedPhases, "Event phases", Comparator.comparing(p -> p.id));
            invalidateInvoker();
        }
        return this;
    }

    @Override
    public void subscribe(EventPriority priority, ResourceLocation phase, T listener) {
        Objects.requireNonNull(priority, "Tried to add a listener with 'null' priority");
        Objects.requireNonNull(phase, "Tried to add a listener to a 'null' event phase");
        Objects.requireNonNull(listener, "Tried to add a 'null' listener");

        synchronized (lock) {
            getOrCreatePhase(phase, true).listeners.add(new Listener<>(phase, priority, listener));
            invalidateInvoker();
        }
    }

    @Override
    public void subscribe(EventPriority priority, T listener) {
        subscribe(priority, DEFAULT_PHASE, listener);
    }

    @Override
    public void subscribe(ResourceLocation phase, T listener) {
        subscribe(EventPriority.NORMAL, phase, listener);
    }

    @Override
    public void subscribe(T listener) {
        subscribe(EventPriority.NORMAL, DEFAULT_PHASE, listener);
    }

    @ApiStatus.Internal
    private Phase getOrCreatePhase(ResourceLocation id, boolean sortIfCreated) {
        Phase phase = phases.get(id);

        if(phase == null) {
            phase = new Phase(id);
            phases.put(id, phase);
            sortedPhases.add(phase);

            if(sortIfCreated) {
                NodeSorting.sort(sortedPhases, "Event phases", Comparator.comparing(p -> p.id));
            }
        }

        return phase;
    }

    @SuppressWarnings("unchecked")
    private T buildInvoker() {
        if(invoker != null) {
            throw new IllegalStateException("Event invoker wasn't invalidated, cannot build a new one");
        }

        List<T> sorted = new ArrayList<>();
        for(Phase phase : sortedPhases) {
            List<Listener<T>> phaseListeners = new ArrayList<>(phase.listeners);
            phaseListeners.sort(Comparator.comparing(Listener::priority));

            phaseListeners.stream().map(Listener::listener).forEach(sorted::add);
        }

        T[] listenerArray = (T[]) sorted.toArray(Object[]::new);
        invoker = invokerFactory.apply(listenerArray);
        return invoker;
    }

    private void invalidateInvoker() {
        this.invoker = null;
    }

    private class Phase extends SortableNode<Phase> {
        private final List<Listener<T>> listeners = new ArrayList<>();

        private final ResourceLocation id;

        private Phase(ResourceLocation id) {
            this.id = id;
        }

        @Override
        protected String getDescription() {
            return id.toString();
        }
    }

    private record Listener<T>(ResourceLocation phase, EventPriority priority, T listener) {
        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }
            if(!(obj instanceof EventBusImpl.Listener<?> other)) {
                return Objects.equals(obj, listener);
            }

            return Objects.equals(other.phase, phase) && Objects.equals(other.priority, priority) && Objects.equals(other.listener, listener);
        }

        @Override
        public int hashCode() {
            return Objects.hash(phase, priority, listener);
        }

        @Override
        public @NotNull String toString() {
            return "Listener[phase=%s, priority=%s, listener=%s]".formatted(phase, priority, listener);
        }
    }
}
