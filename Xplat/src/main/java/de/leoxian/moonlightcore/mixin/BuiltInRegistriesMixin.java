package de.leoxian.moonlightcore.mixin;

import com.mojang.serialization.Lifecycle;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.levelgen.SurfaceRuleRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BuiltInRegistries.class)
public abstract class BuiltInRegistriesMixin {

    @Shadow
    private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> p_259121_, Lifecycle p_259977_, BuiltInRegistries.RegistryBootstrap<T> p_259874_) {
        throw new AssertionError();
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "registerSimple(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/registries/BuiltInRegistries$RegistryBootstrap;)Lnet/minecraft/core/Registry;", at = @At("HEAD"), cancellable = true)
    private static <T> void mlcore_registerSimple(ResourceKey<? extends Registry<T>> key, BuiltInRegistries.RegistryBootstrap<T> bootstrap, CallbackInfoReturnable<Registry<T>> cir) {
        if(key.equals(Registries.MATERIAL_RULE)) {
            cir.setReturnValue(registerSimple(key, Lifecycle.stable(), registry -> {
                bootstrap.run(registry);

                return Registry.register(registry, MoonlightCore.location("merged"), (T) SurfaceRuleRegistry.NamespacedRuleSource.CODEC.codec());
            }));
        }
    }

}
