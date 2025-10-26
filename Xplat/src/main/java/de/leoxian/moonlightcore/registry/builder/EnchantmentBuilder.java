package de.leoxian.moonlightcore.registry.builder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Arrays;
import java.util.EnumSet;

public class EnchantmentBuilder<T extends Enchantment> extends AbstractBuilder<Enchantment, T> {
    public static <T extends Enchantment> EnchantmentBuilder<T> of(ResourceLocation id, EnchantmentCategory category, EnchantmentFactory<T> factory) {
        return new EnchantmentBuilder<>(id, category, factory);
    }

    private final EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);
    private final EnchantmentFactory<T> factory;
    private final EnchantmentCategory category;

    private Enchantment.Rarity rarity = Enchantment.Rarity.COMMON;

    protected EnchantmentBuilder(ResourceLocation id, EnchantmentCategory category, EnchantmentFactory<T> factory) {
        super(Registries.ENCHANTMENT, id);
        this.category = category;
        this.factory = factory;
    }

    public EnchantmentBuilder<T> rarity(Enchantment.Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public EnchantmentBuilder<T> addAmorSlots() {
        return this.addSlots(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
    }

    public EnchantmentBuilder<T> addSlots(EquipmentSlot... slots) {
        this.slots.addAll(Arrays.asList(slots));
        return this;
    }

    @Override
    protected T buildEntry() {
        return this.factory.create(this.rarity, this.category, slots.toArray(new EquipmentSlot[0]));
    }

    @FunctionalInterface
    public interface EnchantmentFactory<T extends Enchantment> {
        T create(Enchantment.Rarity rarity, EnchantmentCategory category, EquipmentSlot... slot);
    }
}
