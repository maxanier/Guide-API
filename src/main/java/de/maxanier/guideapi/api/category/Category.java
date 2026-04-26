package de.maxanier.guideapi.api.category;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.entry.Entry;
import de.maxanier.guideapi.api.entry.EntryBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Map;

public class Category extends CategoryBase {

    /**
     * Set via {@link de.maxanier.guideapi.core.APISetter#setScreenFactories()}
     */
    private static CategoryScreenFactory createCategoryScreen;

    public Category(Map<Identifier, EntryBase> entryList, Component name) {
        super(entryList, name);
    }

    public Category(Component name) {
        super(name);
    }

    @Override
    public boolean canSee(Player player, Book book) {
        return true;
    }

    @Override
    public void draw(GuiGraphics graphics, Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX, int mouseY, GuideBookScreen screen, boolean drawOnLeft) {
    }

    @Override
    public void drawExtras(GuiGraphics graphics, Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX, int mouseY, GuideBookScreen screen, boolean drawOnLeft) {
    }

    @Override
    public void onInit(Book book, Player player) {
    }

    @Override
    public void onLeftClicked(Book book, double mouseX, double mouseY, Player player) {
        Minecraft.getInstance().setScreen(createCategoryScreen.create(book, this, player, null));
    }

    @Override
    public void onRightClicked(Book book, double mouseX, double mouseY, Player player) {
    }

    @FunctionalInterface
    public interface CategoryScreenFactory {
        Screen create(Book book, Category category, Player player, @Nullable Entry entry);
    }
}
