package de.maxanier.guideapi.test;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.util.ModelHelper;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.RegisteredCondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
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
        event.getGenerator().addProvider(true, new TestModRecipeProvider.Runner(event.getGenerator().getPackOutput(), event.getLookupProvider()));
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

    private static class TestModRecipeProvider extends RecipeProvider {

        protected TestModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            super(registries, output);
        }

        @Override
        protected void buildRecipes() {
            shapeless(RecipeCategory.MISC, GuideAPI.getItemForBook(TestBook.book).value()).requires(Items.BOOK).requires(Items.BONE).unlockedBy("has_book", has(Items.BOOK)).unlockedBy("has_bone", has(Items.BONE)).save(output.withConditions(new ModLoadedCondition(GuideMod.ID)));
        }

        public static class Runner extends RecipeProvider.Runner {

            public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
                super(output, lookupProvider);
            }

            @Override
            protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
                return new TestModRecipeProvider(provider, output);
            }

            @Override
            public String getName() {
                return "Test Guidebook Recipes";
            }
        }
    }

}
