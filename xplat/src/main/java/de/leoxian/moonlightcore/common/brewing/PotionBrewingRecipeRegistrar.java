package de.leoxian.moonlightcore.common.brewing;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.world.item.alchemy.PotionBrewing;

import java.util.function.Consumer;

public interface PotionBrewingRecipeRegistrar {
    /// Configure and register custom brewing recipes
    /// @param initializer The initializer
    static void configure(Consumer<PotionBrewing.Builder> initializer) {
        XplatAbstraction.INSTANCE.potionBrewing(initializer);
    }

}
