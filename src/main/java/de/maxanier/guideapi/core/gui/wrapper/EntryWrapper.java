package de.maxanier.guideapi.core.gui.wrapper;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.core.gui.CategoryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;

public class EntryWrapper extends AbstractWrapper {

    public Book book;
    public CategoryBase category;
    public EntryBase entry;
    public int x, y, width, height;
    public Player player;
    public Font renderer;
    public CategoryScreen categoryGui;

    public EntryWrapper(CategoryScreen categoryGui, Book book, CategoryBase category, EntryBase entry, int x, int y, int width, int height, Player player, Font renderer) {
        this.book = book;
        this.category = category;
        this.entry = entry;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.player = player;
        this.renderer = renderer;
        this.categoryGui = categoryGui;
    }

    @Override
    public boolean canPlayerSee() {
        return entry.canSee(player, book);
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, int mouseX, int mouseY, GuideBookScreen gui) {
        entry.draw(graphics, book, category, x, y, width, height, mouseX, mouseY, gui, Minecraft.getInstance().font);
    }

    @Override
    public void drawExtras(GuiGraphicsExtractor graphics, int mouseX, int mouseY, GuideBookScreen gui) {
        entry.drawExtras(graphics, book, category, x, y, width, height, mouseX, mouseY, gui, Minecraft.getInstance().font);
    }

    @Override
    public boolean isMouseOnWrapper(double mouseX, double mouseY) {
        return GuiHelper.isMouseBetween(mouseX, mouseY, x, y, width, height);
    }

    @Override
    public void onHoverOver(int mouseX, int mouseY) {
    }
}
