package de.leoxian.moonlightcore.fabric.common.mixin.event;

import de.leoxian.moonlightcore.common.event.TagsUpdatedEvents;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @Shadow
    @Final
    private ReloadableServerRegistries.Holder fullRegistryHolder;

    @Inject(
            method = "updateComponentsAndStaticRegistryTags",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void moonlightcore$dispatchServerDataLoadEvent(CallbackInfo ci) {
        TagsUpdatedEvents.SERVER_DATA_LOAD.doFire().onServerDataLoad(this.fullRegistryHolder.lookup(), (ReloadableServerResources) (Object) this);
    }
}
