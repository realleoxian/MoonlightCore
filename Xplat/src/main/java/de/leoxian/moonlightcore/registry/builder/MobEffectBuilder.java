/*
 * This file is licensed under the same terms as the rest of the MoonlightCore project.
 * See the root LICENSE file for details. (https://github.com/realleoxian/MoonlightCore/blob/dev/LICENSE.md)
 */
package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.registry.DeferredRegistrar;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MobEffectBuilder<T extends MobEffect> extends AbstractBuilder<MobEffect, T, MobEffectBuilder<T>> {

    public static <T extends MobEffect> MobEffectBuilder<T> builder(DeferredRegistrar<MobEffect> registrar, String name, MobEffectFactory<T> factory, MobEffectCategory category, int color) {
        return new MobEffectBuilder<>(registrar, name, factory, category, color);
    }

    private final List<AttributeModifierEntry> modifiers = new ArrayList<>();

    private final MobEffectFactory<T> factory;
    private final MobEffectCategory category;
    private final int color;

    private Supplier<MobEffectInstance.FactorData> factorDataSupplier;

    protected MobEffectBuilder(DeferredRegistrar<MobEffect> registrar, String name, MobEffectFactory<T> factory, MobEffectCategory category, int color) {
        super(registrar, name);
        this.factory = factory;
        this.category = category;
        this.color = color;
    }

    public MobEffectBuilder<T> factorData(Supplier<MobEffectInstance.FactorData> supplier) {
        this.factorDataSupplier = supplier;
        return this;
    }

    public MobEffectBuilder<T> addModifier(Attribute attribute, String uuid, double amount, AttributeModifier.Operation operation) {
        this.modifiers.add(new AttributeModifierEntry(attribute, uuid, amount, operation));
        return this;
    }

    @Override
    protected T buildEntry() {
        T entry = factory.create(category, color);

        if (factorDataSupplier != null) {
            entry.setFactorDataFactory(factorDataSupplier);
        }

        for (AttributeModifierEntry m : modifiers) {
            entry.addAttributeModifier(m.attribute, m.uuid, m.amount, m.operation);
        }

        return entry;
    }

    public interface MobEffectFactory<T extends MobEffect> {
        T create(MobEffectCategory category, int color);
    }

    private record AttributeModifierEntry(Attribute attribute, String uuid, double amount, AttributeModifier.Operation operation) {}

}