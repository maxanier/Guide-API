package de.maxanier.guideapi.page.reciperenderer;

import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.gui.BaseScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.display.*;

import java.util.List;

public class ShapedRecipesRenderer extends CraftingRecipeRenderer<ShapedRecipe, ShapedCraftingRecipeDisplay> {

    protected List<List<ItemStack>> inputs;

    public ShapedRecipesRenderer(RecipeHolder<ShapedRecipe> recipe) {
        super(recipe, ShapedCraftingRecipeDisplay.class, Component.translatable("guideapi.text.crafting.shaped"));
    }

    @Override
    public void init(ContextMap context) {
        super.init(context);
        inputs = display().map(d -> d.ingredients().stream().map(sd -> sd.resolveForStacks(context)).toList()).orElse(List.of());
    }

    @Override
    public void draw(GuiGraphics graphics, RegistryAccess registryAccess, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj, IngredientCycler cycler) {
        super.draw(graphics, registryAccess, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj, cycler);

        display().ifPresent(d -> {
            for (int y = 0; y < d.height(); y++) {
                for (int x = 0; x < d.width(); x++) {
                    int i = d.width() * y + x;
                    int stackX = (x + 1) * 17 + (guiLeft + 53) + x;
                    int stackY = (y + 1) * 17 + (guiTop + 38) + y;
                    ItemStack s = cycler.getCycledIngredientStack(inputs.get(i), i);
                    GuiHelper.drawItemStack(graphics, s, stackX, stackY);
                    if (GuiHelper.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15))
                        tooltips = GuiHelper.getTooltip(s);
                }
            }
        });

    }
}
