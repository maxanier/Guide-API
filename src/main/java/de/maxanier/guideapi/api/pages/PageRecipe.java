package de.maxanier.guideapi.api.pages;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.LogHelper;
import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.recipes.FurnaceRecipeRenderer;
import de.maxanier.guideapi.api.recipes.IRecipeRenderer;
import de.maxanier.guideapi.api.recipes.ShapedRecipesRenderer;
import de.maxanier.guideapi.api.recipes.ShapelessRecipesRenderer;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.api.util.PageHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;


public class PageRecipe extends Page {
    @SuppressWarnings("unchecked")
    @Nullable
    public static IRecipeRenderer createRenderer(RecipeHolder<?> recipeHolder) {
        Recipe<?> recipe = recipeHolder.value();
        return switch (recipe) {
            case ShapedRecipe shapedRecipe -> new ShapedRecipesRenderer((RecipeHolder<ShapedRecipe>) recipeHolder);
            case ShapelessRecipe shapelessRecipe ->
                    new ShapelessRecipesRenderer((RecipeHolder<ShapelessRecipe>) recipeHolder);
            case SmeltingRecipe smeltingRecipe ->
                    new FurnaceRecipeRenderer((RecipeHolder<SmeltingRecipe>) recipeHolder);
            default -> null;
        };
    }

    private final IngredientCycler ingredientCycler = new IngredientCycler();
    @Nonnull
    private final Function<RecipeHolder<?>, IRecipeRenderer> rendererFactory;
    @Nonnull
    private final Identifier recipeId;
    @Nullable
    public RecipeHolder<?> recipe;
    @Nullable
    public IRecipeRenderer recipeRenderer;
    @Nullable
    private Component invalidWarning;

    public PageRecipe(@Nonnull Identifier recipeId) {
        this(recipeId, PageRecipe::createRenderer);
    }

    public PageRecipe(@NonNull Identifier recipeId, @NonNull Function<RecipeHolder<?>, IRecipeRenderer> rendererFactory) {
        this.recipeId = recipeId;
        this.rendererFactory = rendererFactory;
    }

    @Override
    public void draw(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        super.draw(graphics, book, category, entry, pageLeft, pageTop, mouseX, mouseY, screen, fontRendererObj);
        ingredientCycler.tick(screen.getMinecraft().level.getGameTime());
        if (invalidWarning != null) {
            int centerX = pageLeft + 10;
            int centerY = pageTop + screen.pageHeight() / 3;
            PageHelper.drawFormattedText(graphics, centerX, centerY, invalidWarning, ARGB.color(0, 30, 30));
        } else {
            if (recipeRenderer != null && recipeRenderer.isValid()) {
                recipeRenderer.draw(graphics, book, category, entry, pageLeft, pageTop, mouseX, mouseY, screen, fontRendererObj, ingredientCycler);
            }
        }

    }

    @Override
    public void drawExtras(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        super.drawExtras(graphics, book, category, entry, guiLeft, guiTop, mouseX, mouseY, screen, fontRendererObj);
        if (recipeRenderer != null && recipeRenderer.isValid()) {
            recipeRenderer.drawExtras(graphics, book, category, entry, guiLeft, guiTop, mouseX, mouseY, screen, fontRendererObj);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageRecipe that)) return false;
        if (!Objects.equals(recipeId, that.recipeId)) return false;
        return Objects.equals(recipeRenderer, that.recipeRenderer);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (recipeId.hashCode());
        result = 31 * result + (recipeRenderer != null ? recipeRenderer.hashCode() : 0);
        return result;
    }

    @Override
    public void onInit(RegistryAccess registryAccess, Book book, CategoryBase category, EntryBase entry, Player player) {
        super.onInit(registryAccess, book, category, entry, player);
        this.recipe = GuideMod.PROXY.getClientSyncedRecipes().byKey(ResourceKey.create(Registries.RECIPE, recipeId));
        if (this.recipe != null) {
            recipeRenderer = rendererFactory.apply(this.recipe);
            if (recipeRenderer == null) {
                LogHelper.error("Did not find renderer for recipe {} of type {}", recipeId, this.recipe.getClass());
                invalidWarning = Component.translatable("guideapi.text.recipe_unrenderable");
            } else {
                recipeRenderer.init(SlotDisplayContext.fromLevel(player.level()));
            }
        } else {
            invalidWarning = Component.translatable("guideapi.text.recipe_unavailable");
        }

    }
}
