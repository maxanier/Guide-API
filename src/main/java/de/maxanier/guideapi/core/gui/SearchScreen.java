package de.maxanier.guideapi.core.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class SearchScreen extends BaseScreen {

    @Nonnull
    static List<List<Pair<EntryBase, CategoryBase>>> getMatches(Book book, @Nullable String query, Player player) {
        List<Pair<EntryBase, CategoryBase>> discovered = Lists.newArrayList();

        for (CategoryBase category : book.getCategoryList()) {
            if (!category.canSee(player, book))
                continue;

            for (EntryBase entry : category.entries.values()) {
                if (!entry.canSee(player, book))
                    continue;

                if (Strings.isNullOrEmpty(query) || entry.getName().getString().toLowerCase(Locale.ENGLISH).contains(query.toLowerCase(Locale.ENGLISH)))
                    discovered.add(Pair.of(entry, category));
            }
        }

        return Lists.partition(discovered, 10);
    }


    private final int renderXOffset = 0;
    private final int renderYOffset = 13;
    private final Screen parent;
    private EditBox searchField;
    private List<List<Pair<EntryBase, CategoryBase>>> searchResults;
    private String lastQuery = "";


    public SearchScreen(Book book, Player player, Screen parent) {
        super(book.getTitle(), book, player);
        this.parent = parent;
        this.searchResults = getMatches(book, null, player);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (this.searchField.charTyped(event)) {
            this.updateSearch();
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public int getPageCount() {
        return searchResults.size();
    }

    @Override
    public void init() {
        super.init();
        searchField = new EditBox(font, pageLeft(), pageTop(), 100, 10, Component.translatable("guideapi.button.search"));
        searchField.setBordered(false);
        searchField.setFocused(true);
        searchResults = getMatches(book, null, player());

        addButtons(true, false);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!searchField.isFocused()) {
            return super.keyPressed(event);
        }

        if (event.key() == InputConstants.KEY_ESCAPE)
            searchField.setFocused(false);

        if (searchField.keyPressed(event)) {
            this.updateSearch();
        }

        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
                int entryX = pageLeft() + renderXOffset;
                int entryY = pageTop() + renderYOffset;

                if (!searchResults.isEmpty() && currentPage() >= 0 && currentPage() < searchResults.size()) {
                    List<Pair<EntryBase, CategoryBase>> pageResults = searchResults.get(currentPage());
                    for (Pair<EntryBase, CategoryBase> entry : pageResults) {
                        if (GuiHelper.isMouseBetween(event.x(), event.y(), entryX, entryY, pageWidth(), 10)) {
                            GuideMod.PROXY.openEntry(book, entry.getRight(), entry.getLeft(), player());
                        }
                        entryY += 13;
                    }
                }
            } else if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                if (GuiHelper.isMouseBetween(event.x(), event.y(), searchField.getX(), searchField.getY(), searchField.getInnerWidth(), searchField.getHeight())) {
                    searchField.setValue("");
                    lastQuery = "";
                    searchResults = getMatches(book, "", player());
                    return true;
                } else {
                    minecraft.setScreen(parent);
                    return true;
                }
            }


            return searchField.mouseClicked(event, doubleClick);
        }
        return true;


    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

        graphics.fill(searchField.getX() - 1, searchField.getY() - 1, searchField.getX() + searchField.getInnerWidth() + 1, searchField.getY() + searchField.getHeight() + 1, new Color(166, 166, 166, 128).getRGB());
        graphics.fill(searchField.getX(), searchField.getY(), searchField.getX() + searchField.getInnerWidth(), searchField.getY() + searchField.getHeight(), new Color(58, 58, 58, 128).getRGB());
        searchField.extractRenderState(graphics, mouseX, mouseY, partialTicks);

        int entryX = pageLeft() + renderXOffset;
        int entryY = pageTop() + renderYOffset;

        if (searchResults.size() != 0 && currentPage() >= 0 && currentPage() < searchResults.size()) {
            List<Pair<EntryBase, CategoryBase>> pageResults = searchResults.get(currentPage());
            for (Pair<EntryBase, CategoryBase> entry : pageResults) {
                entry.getLeft().draw(graphics, book, entry.getRight(), entryX, entryY, pageWidth(), 10, mouseX, mouseY, this, font);
                entry.getLeft().drawExtras(graphics, book, entry.getRight(), entryX, entryY, pageWidth(), 10, mouseX, mouseY, this, font);
                if (GuiHelper.isMouseBetween(mouseX, mouseY, entryX, entryY, pageWidth(), 10)) {
                    if (InputConstants.isKeyDown(minecraft.getWindow(), InputConstants.KEY_LSHIFT)) {
                        List<ClientTooltipComponent> tooltips = entry.getRight().getTooltip().stream().map(c -> ClientTooltipComponent.create(c.getVisualOrderText())).toList();
                        graphics.tooltip(font, tooltips, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
                    }
                }

                entryY += 13;
            }
        }


    }

    @Override
    protected void goBack() {
        minecraft.setScreen(parent);
    }

    private void updateSearch() {
        if (!searchField.getValue().equalsIgnoreCase(lastQuery)) {
            lastQuery = searchField.getValue();
            searchResults = getMatches(book, searchField.getValue(), player());
            if (currentPage() > searchResults.size())
                setPage(searchResults.size() - 1);
        }
    }
}
