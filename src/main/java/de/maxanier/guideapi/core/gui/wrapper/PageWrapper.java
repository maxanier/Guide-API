package de.maxanier.guideapi.core.gui.wrapper;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.pages.IPage;
import de.maxanier.guideapi.core.gui.EntryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;

public class PageWrapper extends AbstractWrapper {

    public EntryScreen guiEntry;
    public Book book;
    public CategoryBase category;
    public EntryBase entry;
    public IPage page;
    public int guiLeft, guiTop;
    public Player player;
    public Font renderer;

    public PageWrapper(EntryScreen guiEntry, Book book, CategoryBase category, EntryBase entry, IPage page, int guiLeft, int guiTop, Player player, Font renderer) {
        this.guiEntry = guiEntry;
        this.book = book;
        this.category = category;
        this.entry = entry;
        this.page = page;
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
        this.player = player;
        this.renderer = renderer;
    }

    @Override
    public boolean canPlayerSee() {
        return page.canSee(book, category, entry, player);
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, GuideBookScreen gui) {
        page.draw(graphics, book, category, entry, guiLeft, guiTop, mouseX, mouseY, gui, Minecraft.getInstance().font);
    }

    @Override
    public void drawExtras(GuiGraphics graphics, int mouseX, int mouseY, GuideBookScreen gui) {
        page.drawExtras(graphics, book, category, entry, guiLeft, guiTop, mouseX, mouseY, gui, Minecraft.getInstance().font);
    }

    @Override
    public boolean isMouseOnWrapper(double mouseX, double mouseY) {
        return true;
    }

    @Override
    public void onHoverOver(int mouseX, int mouseY) {
    }
}
