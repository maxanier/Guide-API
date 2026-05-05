package de.maxanier.guideapi.core.gui.wrapper;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.pages.IPage;
import de.maxanier.guideapi.core.gui.EntryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

public class PageWrapper extends AbstractWrapper {

    public EntryScreen guiEntry;
    public Book book;
    public CategoryBase category;
    public EntryBase entry;
    public IPage page;
    public int pageLeft, pageTop;
    public Player player;
    public Font renderer;

    public PageWrapper(EntryScreen guiEntry, Book book, CategoryBase category, EntryBase entry, IPage page, int pageLeft, int pageTop, Player player, Font renderer) {
        this.guiEntry = guiEntry;
        this.book = book;
        this.category = category;
        this.entry = entry;
        this.page = page;
        this.pageLeft = pageLeft;
        this.pageTop = pageTop;
        this.player = player;
        this.renderer = renderer;
    }

    @Override
    public boolean canPlayerSee() {
        return page.canSee(book, category, entry, player);
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, int mouseX, int mouseY, GuideBookScreen gui) {
        page.draw(graphics, book, category, entry, pageLeft, pageTop, mouseX, mouseY, gui, Minecraft.getInstance().font);
    }

    @Override
    public void drawExtras(GuiGraphicsExtractor graphics, int mouseX, int mouseY, GuideBookScreen gui) {
        page.drawExtras(graphics, book, category, entry, pageLeft, pageTop, mouseX, mouseY, gui, Minecraft.getInstance().font);
    }

    @Override
    public boolean isMouseOnWrapper(double mouseX, double mouseY) {
        return true;
    }

    @Override
    public void onHoverOver(int mouseX, int mouseY) {
    }
}
