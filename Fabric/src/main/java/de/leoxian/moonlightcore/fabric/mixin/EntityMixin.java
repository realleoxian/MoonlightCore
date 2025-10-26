package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.event.common.EntityEvent;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyVariable(method = "setLevelCallback", argsOnly = true, ordinal = 0, at = @At("HEAD"))
    public EntityInLevelCallback mlcore_setLevelCallback(EntityInLevelCallback value) {
        if(value == EntityInLevelCallback.NULL) return value;
        if(value == null) return value;
        Entity self = (Entity) (Object) this;

        return new EntityInLevelCallback() {
            private long lastSectionKey = SectionPos.asLong(self.blockPosition());

            @Override
            public void onMove() {
                value.onMove();

                var currentSectionKey = SectionPos.asLong(self.blockPosition());
                if(currentSectionKey != this.lastSectionKey) {
                    EntityEvent.ENTER_SECTION.invoker().onEnterSection(self, SectionPos.of(currentSectionKey), SectionPos.of(lastSectionKey));
                    lastSectionKey = currentSectionKey;
                }
            }

            @Override
            public void onRemove(Entity.RemovalReason reason) {
                value.onRemove(reason);
            }
        };
    }

}
