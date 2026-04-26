package de.maxanier.guideapi.api.pages;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.IngredientCycler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public class PageItemStack extends PageText {

    private final IngredientCycler ingredientCycler = new IngredientCycler();
    public Ingredient ingredient;


    public PageItemStack(FormattedText draw, Ingredient ingredient) {
        super(draw, 60);
        this.ingredient = ingredient;
    }


    /**
     * @param draw - Unlocalized text to draw
     * @param item - Item to render
     */
    public PageItemStack(FormattedText draw, Item item) {
        this(draw, Ingredient.of(item));
    }

    /**
     * @param draw  - Unlocalized text to draw
     * @param block - Block to render
     */
    public PageItemStack(FormattedText draw, Block block) {
        this(draw, Ingredient.of(block));
    }


    @Override
    public void drawExtras(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        ingredientCycler.tick(screen.getMinecraft().level.getGameTime());
        ingredientCycler.getCycledIngredientStack(ingredient, 0).ifPresent(s -> {
            GuiHelper.drawScaledItemStack(graphics, s, guiLeft + 101, guiTop + 20, 3);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageItemStack that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (ingredient != null ? ingredient.hashCode() : 0);
        return result;
    }
}
