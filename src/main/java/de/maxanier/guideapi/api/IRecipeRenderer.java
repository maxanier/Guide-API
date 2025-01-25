package de.maxanier.guideapi.api;

import com.google.common.collect.Lists;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.gui.BaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.List;
import java.util.Optional;

public interface IRecipeRenderer {

    @MustBeInvokedByOverriders
    default void init(ContextMap context) {

    }


    void draw(GuiGraphics graphics, RegistryAccess registryAccess, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj, IngredientCycler cycler);

    default void drawExtras(GuiGraphics graphics, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj) {

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
        public void drawExtras(GuiGraphics graphics, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj) {
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
        private Q recipeDisplay;
        protected List<ItemStack> outputs;
        protected List<ItemStack> craftingStations;

        public RecipeDisplayRenderer(RecipeHolder<T> recipe, Class<Q> recipeDisplayClass) {
            super(recipe);
            List<RecipeDisplay> ds = this.recipe.value().display();
            if (ds.isEmpty()) {
                LogManager.getLogger().warn("No recipe display for {}.", this.recipe.id());
            } else {
                RecipeDisplay d = ds.getFirst();
                if (recipeDisplayClass.isInstance(d)) {
                    recipeDisplay = recipeDisplayClass.cast(d);
                } else {
                    LogManager.getLogger().warn("Invalid recipe display type {} for {}", d, recipe);
                }
            }
        }

        @Override
        public void init(ContextMap context) {
            super.init(context);
            outputs = display().map(d -> d.result().resolveForStacks(context)).orElse(List.of());
            craftingStations = display().map(d -> d.craftingStation().resolveForStacks(context)).orElse(List.of());
        }

        protected Optional<Q> display() {
            return Optional.ofNullable(recipeDisplay);
        }


        @Override
        public boolean isValid() {
            return recipeDisplay != null;
        }
    }
}
