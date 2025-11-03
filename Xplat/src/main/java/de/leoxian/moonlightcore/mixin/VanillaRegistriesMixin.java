package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.registry.DatapackRegistryBuilder;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VanillaRegistries.class)
public class VanillaRegistriesMixin {

    @Shadow
    @Final
    private static RegistrySetBuilder BUILDER;

    @SuppressWarnings("unchecked")
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void mlcore_registerDatapackRegistries(CallbackInfo ci) {
        DatapackRegistryBuilder.bootstrap((key, bootstrap) -> {
            BUILDER.add((ResourceKey) key, (RegistrySetBuilder.RegistryBootstrap) bootstrap);
        });
    }

}
