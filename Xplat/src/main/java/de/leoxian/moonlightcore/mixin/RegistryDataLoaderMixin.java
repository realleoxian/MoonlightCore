package de.leoxian.moonlightcore.mixin;

import com.mojang.serialization.Decoder;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.registry.DatapackRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {

    @Mutable @Accessor
    static void setWORLDGEN_REGISTRIES(List<RegistryDataLoader.RegistryData<?>> list) {}

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void mlcore_clinit(CallbackInfo ci) {
        List<RegistryDataLoader.RegistryData<?>> enhanced = new ArrayList<>(RegistryDataLoader.WORLDGEN_REGISTRIES.size() + 1);
        enhanced.addAll(RegistryDataLoader.WORLDGEN_REGISTRIES);
        MoonlightCore.LOGGER.debug("Enhanced RegistryDataLoader.WORLDGEN_REGISTRIES");

        DatapackRegistryBuilder.forEach((key, codec) -> {
            if(codec != null) {
                MoonlightCore.LOGGER.debug("  - Adding '{}'", key.location());
                enhanced.add(new RegistryDataLoader.RegistryData(key, codec));
            }
        });

        setWORLDGEN_REGISTRIES(enhanced);
    }

    @Inject(method = "loadRegistryContents", at = @At("TAIL"))
    private static <E> void mlcore_loadRegistryContents(RegistryOps.RegistryInfoLookup lookup, ResourceManager manager, ResourceKey<? extends Registry<E>> registryKey, WritableRegistry<E> registry, Decoder<E> decoder, Map<ResourceKey<?>, Exception> exceptions, CallbackInfo ci) {
        DatapackRegistryBuilder.bootstrap(lookup, registryKey, registry);
    }

}
