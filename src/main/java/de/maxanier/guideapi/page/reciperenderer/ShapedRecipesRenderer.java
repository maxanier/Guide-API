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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class ShapedRecipesRenderer extends CraftingRecipeRenderer<ShapedRecipe> {

    public ShapedRecipesRenderer(ShapedRecipe recipe) {
        super(recipe, Component.translatable("guideapi.text.crafting.shaped"));
    }

    @Override
    public void draw(GuiGraphics graphics, RegistryAccess registryAccess, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj, IngredientCycler cycler) {
        super.draw(graphics, registryAccess, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj, cycler);
        for (int y = 0; y < recipe.getRecipeHeight(); y++) {
            for (int x = 0; x < recipe.getRecipeWidth(); x++) {
                int i = y * recipe.getRecipeWidth() + x;
                int stackX = (x + 1) * 17 + (guiLeft + 53) + x;
                int stackY = (y + 1) * 17 + (guiTop + 38) + y;

                Ingredient ingredient = recipe.getIngredients().get(i);
                cycler.getCycledIngredientStack(ingredient, i).ifPresent(s -> {
                    GuiHelper.drawItemStack(graphics, s, stackX, stackY);
                    if (GuiHelper.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15))
                        tooltips = GuiHelper.getTooltip(s);
                });
            }
        }
    }
}
