package de.realleoxian.moonlightcore.api.client.model.plugin;

import de.realleoxian.moonlightcore.api.event.EventBus;
import net.minecraft.world.level.block.Block;

public interface ModelLoadPlugin {
    void initializePlugin(Context context);

    interface Context {
        void registerBlockStateModelModifier(Block block, BlockStateModelModifier modifier);

        EventBus<RegisterModelsLocation> registerModelsLocation();

        EventBus<ModifyUnbaked> modifyUnbakedModel();

        EventBus<ModifyBeforeBake> modifyBeforeBake();

        EventBus<ModifyBakeResult> modifyBakeResult();

        EventBus<AfterBaking> afterBaking();
    }
}
