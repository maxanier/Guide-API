package de.maxanier.guideapi.test;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.util.ModelHelper;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Generate the item models for the testbooks
 */
@EventBusSubscriber(modid = GuideMod.ID, value = Dist.CLIENT)
public class TestModDataProvider {

    @SuppressWarnings("UnreachableCode")
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.getGenerator().addProvider(true, new TestModModelProvider(event.getGenerator().getPackOutput()));
    }

    private static class TestModModelProvider extends ModelProvider {

        public TestModModelProvider(PackOutput output) {
            super(output, GuideMod.ID);
        }

        @Override
        protected @NotNull Stream<? extends Holder<Item>> getKnownItems() {
            return ModelHelper.getBookItems(TestBook.book, TestBook2.book, TestBook3.book);
        }

        @Override
        protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
            ModelHelper.generateDefaultGuidebookModel(itemModels, TestBook.book);
            ModelHelper.generateDefaultGuidebookModel(itemModels, TestBook2.book);
            ModelHelper.generateDefaultGuidebookModel(itemModels, TestBook3.book);
        }

    }

}
