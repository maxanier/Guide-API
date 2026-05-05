package de.maxanier.guideapi.api.recipes;

import com.google.common.collect.Lists;
import de.maxanier.guideapi.LogHelper;
import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.IngredientCycler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.List;
import java.util.Optional;

public interface IRecipeRenderer {

    void draw(GuiGraphicsExtractor graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj, IngredientCycler cycler);

    default void drawExtras(GuiGraphicsExtractor graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {

    }

    @MustBeInvokedByOverriders
    default void init(ContextMap context) {

    }

    /**
     * @return Whether the recipe is valid and should be rendered
     */
    default boolean isValid() {
        return true;
    }

    abstract class RecipeRendererBase<T extends Recipe<?>> implements IRecipeRenderer {

        protected RecipeHolder<T> recipe;
        protected List<Component> tooltips = Lists.newArrayList();

        public RecipeRendererBase(RecipeHolder<T> recipe) {
            this.recipe = recipe;
        }


        @Override
        public void drawExtras(GuiGraphicsExtractor graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font,
                    tooltips,
                    Optional.empty(),
                    mouseX,
                    mouseY,
                    null
            );
            tooltips.clear();
        }
    }

    abstract class RecipeDisplayRenderer<T extends Recipe<?>, Q extends RecipeDisplay> extends RecipeRendererBase<T> {
        protected List<ItemStack> outputs;
        protected List<ItemStack> craftingStations;
        private Q recipeDisplay;

        public RecipeDisplayRenderer(RecipeHolder<T> recipe, Class<Q> recipeDisplayClass) {
            super(recipe);
            List<RecipeDisplay> ds = this.recipe.value().display();
            if (ds.isEmpty()) {
                LogHelper.info("No recipe display for " + this.recipe.id());
            } else {
                RecipeDisplay d = ds.getFirst();
                if (recipeDisplayClass.isInstance(d)) {
                    recipeDisplay = recipeDisplayClass.cast(d);
                } else {
                    LogHelper.error("Invalid recipe display type {} for {}", d, recipe);
                }
            }
        }

        @Override
        public void init(ContextMap context) {
            super.init(context);
            outputs = display().map(d -> d.result().resolveForStacks(context)).orElse(List.of());
            craftingStations = display().map(d -> d.craftingStation().resolveForStacks(context)).orElse(List.of());
        }

        @Override
        public boolean isValid() {
            return recipeDisplay != null;
        }

        protected Optional<Q> display() {
            return Optional.ofNullable(recipeDisplay);
        }
    }
}
