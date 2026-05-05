package de.maxanier.guideapi.api.recipes;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.api.util.SubTexture;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

public abstract class CraftingRecipeRenderer<T extends Recipe<?>, Q extends RecipeDisplay> extends IRecipeRenderer.RecipeDisplayRenderer<T, Q> {


    private final Component title;
    private Component customDisplay;

    public CraftingRecipeRenderer(RecipeHolder<T> recipe, Class<Q> recipeDisplayClass, Component title) {
        super(recipe, recipeDisplayClass);
        this.title = title;
    }

    @Override
    public void draw(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen guiBase, Font fontRendererObj, IngredientCycler cycler) {

        SubTexture.CRAFTING_GRID.draw(graphics, pageLeft - 39 + 68, pageTop - 13 + 53);

        Component recipeName = customDisplay == null ? title : customDisplay;
        GuiHelper.drawCenteredStringWithoutShadow(graphics, fontRendererObj, recipeName, guiBase.pageXCenter(), pageTop, book.getTextColor());


        int stationX = pageLeft - 39 + 125;
        int stationY = pageTop - 13 + 55;

        ItemStack c = cycler.getCycledIngredientStack(this.craftingStations, -2);
        GuiHelper.drawItemStack(graphics, c, stationX, stationY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, stationX, stationY, 15, 15))
            tooltips = GuiHelper.getTooltip(c);


        int outputX = pageLeft - 39 + 148;
        int outputY = pageTop - 13 + 73;
        ItemStack s = cycler.getCycledIngredientStack(this.outputs, -1);
        GuiHelper.drawItemStack(graphics, s, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15))
            tooltips = GuiHelper.getTooltip(s);

    }

    public void setCustomTitle(Component customDisplay) {
        this.customDisplay = customDisplay;
    }


}
