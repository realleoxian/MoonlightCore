package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class EntityEvents {
    public static final EventBus<Addition> ADDITION = EventBus.create(Addition.class, (listeners) -> (level, entity) -> {
        for(Addition listener : listeners) {
            listener.onEntityAddition(level, entity);
        }
    });
    public static final EventBus<Remove> REMOVE = EventBus.create(Remove.class, (listeners) -> (level, entity) -> {
        for(Remove listener : listeners) {
            listener.onEntityRemove(level, entity);
        }
    });
    public static final EventBus<EnterSection> ENTER_SECTION = EventBus.create(EnterSection.class, (listeners) -> (level, entity, packedPreviousSection, packedCurrentSection) -> {
       for(EnterSection listener : listeners) {
           listener.onEntityEnterSection(level, entity, packedPreviousSection, packedCurrentSection);
       }
    });

    private EntityEvents() {}

    public interface Addition {
        /**
         * Invoked whenever an entity is added to the level
         * @param level     The level the entity its in
         * @param entity    The entity that was added
         */
        void onEntityAddition(Level level, Entity entity);
    }

    public interface Remove {
        /**
         * Invoked whenever an entity is removed from the level
         * @param level     The level the entity its in
         * @param entity    The entity that was removed
         */
        void onEntityRemove(Level level, Entity entity);
    }

    public interface EnterSection {
        /**
         * Invoked whenever an entity enters a chunk
         * @param level                     The level the entity is in
         * @param entity                    The entity that entered to a new chunk
         * @param packedPreviousSection     The packed version of the previous section, use {@link net.minecraft.core.SectionPos#of(long)} to unpack it
         * @param packedCurrentSection      The packed version of the current section, use {@link net.minecraft.core.SectionPos#of(long)} to unpack it
         */
        void onEntityEnterSection(Level level, Entity entity, long packedPreviousSection, long packedCurrentSection);
    }
}
