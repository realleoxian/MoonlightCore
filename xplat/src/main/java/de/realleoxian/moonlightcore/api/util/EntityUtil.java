package de.realleoxian.moonlightcore.api.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class EntityUtil {
    @Nullable
    public static Entity getEntityByUUID(Level level, UUID entityId) {
        if (level instanceof ServerLevel serverLevel) {
            for (var entity : serverLevel.getAllEntities()) {
                if (entity.getUUID().equals(entityId))
                    return entity;
            }
        }
        return null;
    }

    private EntityUtil() {}
}
