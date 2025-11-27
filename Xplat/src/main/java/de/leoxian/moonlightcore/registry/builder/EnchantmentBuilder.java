package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.registry.DeferredRegistrar;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Arrays;
import java.util.EnumSet;

public class EnchantmentBuilder<T extends Enchantment> extends AbstractBuilder<Enchantment, T, EnchantmentBuilder<T>> {

    private final EnchantmentCategory category;
    private final EnchantmentFactory<T> factory;

    private Enchantment.Rarity rarity = Enchantment.Rarity.COMMON;
    private EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);

    protected EnchantmentBuilder(DeferredRegistrar<Enchantment> registrar, String name, EnchantmentCategory category, EnchantmentFactory<T> factory) {
        super(registrar, name);
        this.category = category;
        this.factory = factory;
    }

    public EnchantmentBuilder<T> rarity(Enchantment.Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public EnchantmentBuilder<T> addSlots(EquipmentSlot... slots) {
        this.slots.addAll(Arrays.asList(slots));
        return this;
    }

    public EnchantmentBuilder<T> addArmorSlots() {
        return addSlots(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
    }

    @Override
    protected T buildEntry() {
        return factory.create(rarity, category, slots.toArray(EquipmentSlot[]::new));
    }

    @FunctionalInterface
    public interface EnchantmentFactory<T extends Enchantment> {
        T create(Enchantment.Rarity rarity, EnchantmentCategory category, EquipmentSlot... slots);
    }

}
