package de.leoxian.moonlightcore.neoforge.common.level.biome.modifier;

import com.mojang.serialization.MapCodec;
import de.leoxian.moonlightcore.common.level.biome.modifier.BiomeModificationPhase;
import de.leoxian.moonlightcore.common.level.biome.modifier.BiomeSelector;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public class MoonlightCoreBiomeModifier implements net.neoforged.neoforge.common.world.BiomeModifier {
    public static final MoonlightCoreBiomeModifier INSTANCE = new MoonlightCoreBiomeModifier();
    public static final MapCodec<MoonlightCoreBiomeModifier> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        BiomeModificationPhase mlPhase = switch (phase){
            case ADD -> BiomeModificationPhase.ADDITION;
            case REMOVE -> BiomeModificationPhase.REMOVAL;
            default -> BiomeModificationPhase.POST_PROCESSING;
        };

        var modifications = NeoforgeBiomeModification.MODIFICATIONS.get(mlPhase);
        if (modifications == null) {
            return;
        }

        for (final var entry : modifications) {
            final BiomeSelector selector = entry.getFirst();
            if (!selector.test(biome)) {
                continue;
            }

            entry.getSecond().modifyBiome(biome, new NeoforgeBiomeModification.Context(builder));
        }
    }

    @Override
    public MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier> codec() {
        return CODEC;
    }
}
