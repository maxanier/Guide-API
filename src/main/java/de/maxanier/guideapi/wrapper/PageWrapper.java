package de.maxanier.guideapi.wrapper;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxanier.guideapi.api.IPage;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.gui.BaseScreen;
import de.maxanier.guideapi.gui.EntryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PageWrapper extends AbstractWrapper {

    public EntryScreen guiEntry;
    public Book book;
    public CategoryAbstract category;
    public EntryAbstract entry;
    public IPage page;
    public int guiLeft, guiTop;
    public PlayerEntity player;
    public FontRenderer renderer;
    public ItemStack bookStack;

    public PageWrapper(EntryScreen guiEntry, Book book, CategoryAbstract category, EntryAbstract entry, IPage page, int guiLeft, int guiTop, PlayerEntity player, FontRenderer renderer, ItemStack bookStack) {
        this.guiEntry = guiEntry;
        this.book = book;
        this.category = category;
        this.entry = entry;
        this.page = page;
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
        this.player = player;
        this.renderer = renderer;
        this.bookStack = bookStack;
    }

    @Override
    public void onHoverOver(int mouseX, int mouseY) {
    }

    @Override
    public boolean canPlayerSee() {
        return page.canSee(book, category, entry, player, bookStack, guiEntry);
    }

    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY, BaseScreen gui) {
        page.draw(stack, book, category, entry, guiLeft, guiTop, mouseX, mouseY, gui, Minecraft.getInstance().fontRenderer);
    }

    @Override
    public void drawExtras(MatrixStack stack, int mouseX, int mouseY, BaseScreen gui) {
        page.drawExtras(stack, book, category, entry, guiLeft, guiTop, mouseX, mouseY, gui, Minecraft.getInstance().fontRenderer);
    }

    @Override
    public boolean isMouseOnWrapper(double mouseX, double mouseY) {
        return true;
    }
}
