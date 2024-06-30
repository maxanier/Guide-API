package de.maxanier.guideapi.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.button.ButtonBack;
import de.maxanier.guideapi.button.ButtonNext;
import de.maxanier.guideapi.button.ButtonPrev;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SearchScreen extends BaseScreen {

    @Nonnull
    static List<List<Pair<EntryAbstract, CategoryAbstract>>> getMatches(Book book, @Nullable String query, Player player, ItemStack bookStack) {
        List<Pair<EntryAbstract, CategoryAbstract>> discovered = Lists.newArrayList();

        for (CategoryAbstract category : book.getCategoryList()) {
            if (!category.canSee(player, bookStack))
                continue;

            for (EntryAbstract entry : category.entries.values()) {
                if (!entry.canSee(player, bookStack))
                    continue;

                if (Strings.isNullOrEmpty(query) || entry.getName().getString().toLowerCase(Locale.ENGLISH).contains(query.toLowerCase(Locale.ENGLISH)))
                    discovered.add(Pair.of(entry, category));
            }
        }

        return Lists.partition(discovered, 10);
    }

    private final Book book;
    private final ResourceLocation outlineTexture;
    private final ResourceLocation pageTexture;
    private final int renderXOffset = 37;
    private final int renderYOffset = 30;
    private final Screen parent;
    private ButtonNext buttonNext;
    private ButtonPrev buttonPrev;
    private EditBox searchField;
    private List<List<Pair<EntryAbstract, CategoryAbstract>>> searchResults;
    private int currentPage = 0;
    private String lastQuery = "";


    public SearchScreen(Book book, Player player, ItemStack bookStack, Screen parent) {
        super(book.getTitle(), player, bookStack);

        this.book = book;
        this.pageTexture = book.getPageTexture();
        this.outlineTexture = book.getOutlineTexture();
        this.parent = parent;
        this.searchResults = getMatches(book, null, player, bookStack);
    }

    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        if (this.searchField.charTyped(p_charTyped_1_, p_charTyped_2_)) {
            this.updateSearch();
            return true;
        }
        return super.charTyped(p_charTyped_1_, p_charTyped_2_);
    }

    @Override
    public void init() {

        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;

        addRenderableWidget(new ButtonBack(guiLeft + xSize / 6, guiTop, (btn) -> {
            minecraft.setScreen(parent);

        }, this));
        addRenderableWidget(buttonNext = new ButtonNext(guiLeft + 4 * xSize / 6, guiTop + 5 * ySize / 6, (btn) -> {
            if (currentPage <= searchResults.size() - 1)
                currentPage++;
        }, this));
        addRenderableWidget(buttonPrev = new ButtonPrev(guiLeft + xSize / 5, guiTop + 5 * ySize / 6, (btn) -> {
            if (currentPage > 0)
                currentPage--;
        }, this));

        searchField = new EditBox(font, guiLeft + 43, guiTop + 12, 100, 10, Component.translatable("guideapi.button.search"));
        searchField.setBordered(false);
        searchField.setFocused(true);
        searchResults = getMatches(book, null, player, bookStack);
    }

    @Override
    public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (!searchField.isFocused()) {
            return super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE)
            searchField.setFocused(false);

        if (searchField.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_)) {
            this.updateSearch();
        }

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int typeofClick) {
        if (!super.mouseClicked(mouseX, mouseY, typeofClick)) {
            if (typeofClick == 0) {
                int entryX = guiLeft + renderXOffset;
                int entryY = guiTop + renderYOffset;

                if (searchResults.size() != 0 && currentPage >= 0 && currentPage < searchResults.size()) {
                    List<Pair<EntryAbstract, CategoryAbstract>> pageResults = searchResults.get(currentPage);
                    for (Pair<EntryAbstract, CategoryAbstract> entry : pageResults) {
                        if (GuiHelper.isMouseBetween(mouseX, mouseY, entryX, entryY, 4 * xSize / 6, 10)) {
                            GuideMod.PROXY.openEntry(book, entry.getRight(), entry.getLeft(), player, bookStack);
                        }
                        entryY += 13;
                    }
                }
            } else if (typeofClick == 1) {
                if (GuiHelper.isMouseBetween(mouseX, mouseY, searchField.getX(), searchField.getY(), searchField.getInnerWidth(), searchField.getHeight())) {
                    searchField.setValue("");
                    lastQuery = "";
                    searchResults = getMatches(book, "", player, bookStack);
                    return true;
                } else {
                    minecraft.setScreen(parent);
                    return true;
                }
            }


            return searchField.mouseClicked(mouseX, mouseY, typeofClick);
        }
        return true;


    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if (pScrollY < 0 && buttonNext.visible && currentPage <= searchResults.size())
            currentPage++;
        else if (pScrollY > 0 && buttonPrev.visible && currentPage > 0)
            currentPage--;

        return pScrollY != 0 || super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);

    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(pageTexture, guiLeft, guiTop, 0, 0, xSize, ySize);
        graphics.setColor((float) book.getColor().getRed() / 255F, (float) book.getColor().getGreen() / 255F, (float) book.getColor().getBlue() / 255F, 1f);
        graphics.blit(outlineTexture, guiLeft, guiTop, 0, 0, xSize, ySize);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        graphics.fill(searchField.getX() - 1, searchField.getY() - 1, searchField.getX() + searchField.getInnerWidth() + 1, searchField.getY() + searchField.getHeight() + 1, new Color(166, 166, 166, 128).getRGB());
        graphics.fill(searchField.getX(), searchField.getY(), searchField.getX() + searchField.getInnerWidth(), searchField.getY() + searchField.getHeight(), new Color(58, 58, 58, 128).getRGB());
        searchField.render(graphics, mouseX, mouseY, partialTicks);

        int entryX = guiLeft + renderXOffset;
        int entryY = guiTop + renderYOffset;

        if (searchResults.size() != 0 && currentPage >= 0 && currentPage < searchResults.size()) {
            List<Pair<EntryAbstract, CategoryAbstract>> pageResults = searchResults.get(currentPage);
            for (Pair<EntryAbstract, CategoryAbstract> entry : pageResults) {
                entry.getLeft().draw(graphics, Minecraft.getInstance().level.registryAccess(), book, entry.getRight(), entryX, entryY, 4 * xSize / 6, 10, mouseX, mouseY, this, font);
                entry.getLeft().drawExtras(graphics, book, entry.getRight(), entryX, entryY, 4 * xSize / 6, 10, mouseX, mouseY, this, font);

                if (GuiHelper.isMouseBetween(mouseX, mouseY, entryX, entryY, 4 * xSize / 6, 10)) {
                    if (GLFW.glfwGetKey(minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS)
                        graphics.renderTooltip(font, entry.getRight().getTooltip(), Optional.empty(), mouseX, mouseY);
                }

                entryY += 13;
            }
        }

        buttonPrev.visible = currentPage != 0;
        buttonNext.visible = currentPage != searchResults.size() - 1 && !searchResults.isEmpty();

    }

    private void updateSearch() {
        if (!searchField.getValue().equalsIgnoreCase(lastQuery)) {
            lastQuery = searchField.getValue();
            searchResults = getMatches(book, searchField.getValue(), player, bookStack);
            if (currentPage > searchResults.size())
                currentPage = searchResults.size() - 1;
        }
    }
}
