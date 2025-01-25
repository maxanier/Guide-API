package de.maxanier.guideapi.page.reciperenderer;

import de.maxanier.guideapi.api.IRecipeRenderer;
import de.maxanier.guideapi.api.SubTexture;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.gui.BaseScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public abstract class CraftingRecipeRenderer<T extends Recipe<?>, Q extends RecipeDisplay> extends IRecipeRenderer.RecipeDisplayRenderer<T, Q> {


    private final Component title;
    private Component customDisplay;

    public CraftingRecipeRenderer(RecipeHolder<T> recipe, Class<Q> recipeDisplayClass, Component title) {
        super(recipe, recipeDisplayClass);
        this.title = title;
    }

    @Override
    public void draw(GuiGraphics graphics, RegistryAccess registryAccess, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj, IngredientCycler cycler) {

        SubTexture.CRAFTING_GRID.draw(graphics, guiLeft + 68, guiTop + 53);

        Component recipeName = customDisplay == null ? title : customDisplay;
        guiBase.drawCenteredStringWithoutShadow(graphics, fontRendererObj, recipeName, guiLeft + guiBase.xSize / 2, guiTop + 12);


        int stationX = guiLeft + 125;
        int stationY = guiTop + 55;

        ItemStack c = cycler.getCycledIngredientStack(this.craftingStations, -2);
        GuiHelper.drawItemStack(graphics, c, stationX, stationY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, stationX, stationY, 15, 15))
            tooltips = GuiHelper.getTooltip(c);


        int outputX = guiLeft + 148;
        int outputY = guiTop + 73;
        ItemStack s = cycler.getCycledIngredientStack(this.outputs, -1);
        GuiHelper.drawItemStack(graphics, s, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15))
            tooltips = GuiHelper.getTooltip(s);

    }

    public void setCustomTitle(Component customDisplay) {
        this.customDisplay = customDisplay;
    }


}
