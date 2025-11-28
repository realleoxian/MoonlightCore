package de.leoxian.moonlightcore.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public abstract class BaseMobEffect extends MobEffect {

    public BaseMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

}
