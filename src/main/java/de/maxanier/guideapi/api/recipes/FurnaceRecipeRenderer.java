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
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;

import java.util.List;


public class FurnaceRecipeRenderer extends IRecipeRenderer.RecipeDisplayRenderer<SmeltingRecipe, FurnaceRecipeDisplay> {
    private final Component title = Component.translatable("guideapi.text.furnace.smelting");

    private List<ItemStack> input;

    public FurnaceRecipeRenderer(RecipeHolder<SmeltingRecipe> recipe) {
        super(recipe, FurnaceRecipeDisplay.class);
    }

    @Override
    public void draw(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen guiBase, Font fontRendererObj, IngredientCycler cycler) {
        SubTexture.FURNACE_GRID.draw(graphics, guiLeft + 90, guiTop + 71);

        GuiHelper.drawCenteredStringWithoutShadow(graphics, fontRendererObj, title, guiLeft + guiBase.xSize() / 2, guiTop + 12, book.getTextColor());

        int x = guiLeft + 92;
        int y = guiTop + 77;

        ItemStack s = cycler.getCycledIngredientStack(input, 0);

        GuiHelper.drawItemStack(graphics, s, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15))
            tooltips = GuiHelper.getTooltip(s);

        ItemStack output = cycler.getCycledIngredientStack(outputs, -1);

        int x2 = guiLeft + 135;
        GuiHelper.drawItemStack(graphics, output, x2, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x2, y, 15, 15))
            tooltips = GuiHelper.getTooltip(output);

    }

    @Override
    public void init(ContextMap context) {
        super.init(context);
        input = display().map(d -> d.ingredient().resolveForStacks(context)).orElse(List.of());
    }
}
