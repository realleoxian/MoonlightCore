package de.leoxian.moonlightcore.core;

import de.leoxian.moonlightcore.api.config.ModConfigSpec;

public class ModConfig {
    private final ModConfigSpec spec;

    private final ModConfigSpec.ConfigCategory test;
    private final ModConfigSpec.ConfigCategory testSubCategory;

    public ModConfig(ModConfigSpec.Builder builder) {
        builder.pushCategory("test", "Example category description", this::testCategory);

        this.spec = builder.build(MoonlightCore.MOD_ID, ModConfigSpec.Side.SERVER);

        this.test = this.spec.getCategory("test").orElseThrow();
        this.testSubCategory = this.test.getChild("subCategory").orElseThrow();
    }

    private void testCategory(ModConfigSpec.ConfigCategory.Builder builder) {
        builder.pushChild("subCategory", "", this::testSubCategory);

        builder.defineBoolean("testBool", () -> false);
    }

    private void testSubCategory(ModConfigSpec.ConfigCategory.Builder builder) {
        builder.description("Sub category int description wao");
        builder.defineInt("subCategoryInt", () -> 1);
    }

    public boolean testBool() {
        return this.test.<Boolean>getUncheckedKey("testBool").orElseThrow().get();
    }

    public int subCategoryInt() {
        return this.testSubCategory.<Integer>getUncheckedKey("subCategoryInt").orElseThrow().get();
    }

}
