package de.maxanier.guideapi.page;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxanier.guideapi.api.IRecipeRenderer;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.Page;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.gui.BaseScreen;
import de.maxanier.guideapi.gui.EntryScreen;
import de.maxanier.guideapi.page.reciperenderer.FurnaceRecipeRenderer;
import de.maxanier.guideapi.page.reciperenderer.ShapedRecipesRenderer;
import de.maxanier.guideapi.page.reciperenderer.ShapelessRecipesRenderer;
import de.maxanier.guideapi.util.LogHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;


public class PageIRecipe extends Page {

    public IRecipe<?> recipe;
    public IRecipeRenderer iRecipeRenderer;
    protected boolean isValid;
    private final IngredientCycler ingredientCycler = new IngredientCycler();

    /**
     * Use this if you are creating a page for a standard recipe, one of:
     * <p>
     * <ul>
     * <li>{@link ShapedRecipe}</li>
     * <li>{@link ShapelessRecipe}</li>
     * <li>{@link FurnaceRecipe}</li>
     * </ul>
     *
     * @param recipe - Recipe to draw
     */
    public PageIRecipe(IRecipe<?> recipe) {
        this(recipe, getRenderer(recipe));
    }

    /**
     * @param recipe          - Recipe to draw
     * @param iRecipeRenderer - Your custom Recipe drawer
     */
    public PageIRecipe(IRecipe<?> recipe, IRecipeRenderer iRecipeRenderer) {
        this.recipe = recipe;
        this.iRecipeRenderer = iRecipeRenderer;
        isValid = recipe != null && iRecipeRenderer != null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(MatrixStack stack, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, FontRenderer fontRendererObj) {
        if (isValid) {
            super.draw(stack, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
            ingredientCycler.tick(guiBase.getMinecraft());
            iRecipeRenderer.draw(stack, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj, ingredientCycler);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawExtras(MatrixStack stack, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, FontRenderer fontRendererObj) {
        if (isValid) {
            super.drawExtras(stack, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
            iRecipeRenderer.drawExtras(stack, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
        }
    }

    @Override
    public boolean canSee(Book book, CategoryAbstract category, EntryAbstract entry, PlayerEntity player, ItemStack bookStack, EntryScreen guiEntry) {
        return isValid;
    }


    @Nullable
    public static IRecipeRenderer getRenderer(IRecipe<?> recipe) {
        if (recipe == null) {
            LogHelper.error("Cannot get renderer for null recipe.");
            return null;
        } else if (recipe instanceof ShapedRecipe) {
            return new ShapedRecipesRenderer((ShapedRecipe) recipe);
        } else if (recipe instanceof ShapelessRecipe) {
            return new ShapelessRecipesRenderer((ShapelessRecipe) recipe);
        } else if (recipe instanceof FurnaceRecipe) {
            return new FurnaceRecipeRenderer((FurnaceRecipe) recipe);
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageIRecipe)) return false;
        if (!super.equals(o)) return false;

        PageIRecipe that = (PageIRecipe) o;

        if (recipe != null ? !recipe.equals(that.recipe) : that.recipe != null) return false;
        return iRecipeRenderer != null ? iRecipeRenderer.equals(that.iRecipeRenderer) : that.iRecipeRenderer == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (recipe != null ? recipe.hashCode() : 0);
        result = 31 * result + (iRecipeRenderer != null ? iRecipeRenderer.hashCode() : 0);
        return result;
    }
}
