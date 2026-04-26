package de.maxanier.guideapi.api.pages;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.api.util.SubTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TODO Untested
 */
public class PageBrewingRecipe extends Page {

    private final IngredientCycler cycler = new IngredientCycler();
    public BrewingRecipe recipe;
    public Ingredient ingredient;
    public Ingredient input;
    public ItemStack output;

    /**
     * Your brewing recipe - what you pass to BrewingRecipeRegistry.addRecipe
     *
     */
    public PageBrewingRecipe(BrewingRecipe recipe) {
        this.recipe = recipe;
        this.ingredient = recipe.getIngredient();
        this.input = recipe.getInput();
        this.output = recipe.getOutput();
    }

    /**
     * @param input      - The top slot of our brewing recipe
     * @param ingredient - What goes in the three bottle slots
     * @param output     - Result of recipe
     */
    public PageBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        this.input = input;
        this.output = output;
        this.ingredient = ingredient;
    }

    @Override
    public void draw(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        cycler.tick(screen.getMinecraft().level.getGameTime());

        int xStart = guiLeft + 88;
        int yStart = guiTop + 52;

        ContextMap contextmap = SlotDisplayContext.fromLevel(screen.getMinecraft().level);


        SubTexture.POTION_GRID.draw(graphics, xStart, yStart);

        List<Component> badTip = new ArrayList<>();
        badTip.add(Component.translatable("guideapi.text.brewing.error"));

        GuiHelper.drawCenteredStringWithoutShadow(graphics, fontRendererObj, Component.translatable("guideapi.text.brewing.brew"), guiLeft + screen.xSize() / 2, guiTop + 12, book.getTextColor());

        //int xmiddle =  guiLeft + guiBase.xSize / 2 - 6;
        int x = xStart + 25;//since item stack is approx 16 wide
        int y = yStart + 1;
        //start input
        int finalX = x;
        int finalY = y;
        cycler.getCycledIngredientStack(ingredient, 0).ifPresent(s -> {
            GuiHelper.drawItemStack(graphics, s, finalX, finalY);
        });

        ItemStack s = input.display().resolveForFirstStack(contextmap); //TODO check and cycle

        List<Component> tooltip = null;
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15))
            tooltip = GuiHelper.getTooltip(s);

        //the three bottles
        y += 39;
        GuiHelper.drawItemStack(graphics, s, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15))
            tooltip = GuiHelper.getTooltip(s);
        int hSpacing = 24;
        x -= hSpacing;
        y -= 8;
        GuiHelper.drawItemStack(graphics, s, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15))
            tooltip = GuiHelper.getTooltip(s);
        x += hSpacing * 2;
        GuiHelper.drawItemStack(graphics, s, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15))
            tooltip = GuiHelper.getTooltip(s);

        if (output.isEmpty())
            output = new ItemStack(Blocks.BARRIER);

        //start output
        x = xStart + 25;
        y += 31;
        GuiHelper.drawItemStack(graphics, output, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15))
            tooltip = output.getItem() == Item.byBlock(Blocks.BARRIER) ? badTip : GuiHelper.getTooltip(output);

        if (output.getItem() == Item.byBlock(Blocks.BARRIER))
            GuiHelper.drawCenteredStringWithoutShadow(graphics, fontRendererObj, Component.translatable("guideapi.text.brewing.error"), guiLeft + screen.xSize() / 2, guiTop + 4 * screen.ySize() / 6, 0xED073D);

        if (tooltip != null) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font,
                    tooltip,
                    Optional.empty(),
                    mouseX,
                    mouseY,
                    null
            );
        }
    }


}
