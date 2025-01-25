package de.maxanier.guideapi.page;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.IRecipeRenderer;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.Page;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.IngredientCycler;
import de.maxanier.guideapi.api.util.PageHelper;
import de.maxanier.guideapi.gui.BaseScreen;
import de.maxanier.guideapi.gui.EntryScreen;
import de.maxanier.guideapi.page.reciperenderer.FurnaceRecipeRenderer;
import de.maxanier.guideapi.page.reciperenderer.ShapedRecipesRenderer;
import de.maxanier.guideapi.page.reciperenderer.ShapelessRecipesRenderer;
import de.maxanier.guideapi.util.LogHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
    @Nullable
    public RecipeHolder<?> recipe;
    @Nullable
    public IRecipeRenderer recipeRenderer;
    @Nonnull
    private final Function<RecipeHolder<?>, IRecipeRenderer> rendererFactory;
    @Nonnull
    private final Identifier recipeId;
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
    public void onInit(Book book, CategoryAbstract category, EntryAbstract entry, Player player, ItemStack bookStack, EntryScreen guiEntry) {
        super.onInit(book, category, entry, player, bookStack, guiEntry);
        this.recipe = GuideMod.PROXY.getClientSyncedRecipes().byKey(ResourceKey.create(Registries.RECIPE, recipeId));
        if (this.recipe != null) {
            recipeRenderer = rendererFactory.apply(this.recipe);
            if (recipeRenderer == null) {
                LogHelper.error("Did not find renderer for recipe {} of type {}", recipeId, this.recipe.getClass());
                invalidWarning = Component.translatable("guideapi.text.recipe_unrenderable");
            } else {
                recipeRenderer.init(SlotDisplayContext.fromLevel(guiEntry.getMinecraft().level));
            }
        } else {
            invalidWarning = Component.translatable("guideapi.text.recipe_unavailable");
        }

    }

    @Override
    public void draw(GuiGraphics graphics, RegistryAccess registryAccess, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj) {
        super.draw(graphics, registryAccess, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
        ingredientCycler.tick(guiBase.getMinecraft().level.getGameTime());
        if (invalidWarning != null) {
            int centerX = guiLeft + 44;
            int centerY = guiTop + guiBase.ySize / 3;
            PageHelper.drawFormattedText(graphics, centerX, centerY, guiBase, invalidWarning, ARGB.color(0, 30, 30));
        } else {
            if (recipeRenderer != null && recipeRenderer.isValid()) {
                recipeRenderer.draw(graphics, registryAccess, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj, ingredientCycler);
            }
        }

    }

    @Override
    public void drawExtras(GuiGraphics graphics, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj) {
        super.drawExtras(graphics, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
        if (recipeRenderer != null && recipeRenderer.isValid()) {
            recipeRenderer.drawExtras(graphics, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
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
}
