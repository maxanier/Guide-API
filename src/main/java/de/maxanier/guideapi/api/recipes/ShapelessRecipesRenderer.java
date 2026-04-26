package de.maxanier.guideapi.api.recipes;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;

import java.util.List;

public class ShapelessRecipesRenderer extends CraftingRecipeRenderer<ShapelessRecipe, ShapelessCraftingRecipeDisplay> {

    protected List<List<ItemStack>> inputs;


    public ShapelessRecipesRenderer(RecipeHolder<ShapelessRecipe> recipe) {
        super(recipe, ShapelessCraftingRecipeDisplay.class, Component.translatable("guideapi.text.crafting.shapeless"));
    }

    @Override
    public void draw(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen guiBase, Font fontRendererObj, IngredientCycler cycler) {
        super.draw(graphics, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj, cycler);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int i = 3 * y + x;
                if (i < inputs.size()) {
                    int stackX = (x + 1) * 17 + (guiLeft + 53) + x;
                    int stackY = (y + 1) * 17 + (guiTop + 38) + y;
                    ItemStack s = cycler.getCycledIngredientStack(inputs.get(i), i);

                    GuiHelper.drawItemStack(graphics, s, stackX, stackY);
                    if (GuiHelper.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15))
                        tooltips = GuiHelper.getTooltip(s);
                }
            }
        }

    }

    @Override
    public void init(ContextMap context) {
        super.init(context);
        inputs = display().map(d -> d.ingredients().stream().map(sd -> sd.resolveForStacks(context)).toList()).orElse(List.of());
    }

}
