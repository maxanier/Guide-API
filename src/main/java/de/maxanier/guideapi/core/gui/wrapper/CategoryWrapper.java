package de.maxanier.guideapi.core.gui.wrapper;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CategoryWrapper extends AbstractWrapper {

    public Book book;
    public CategoryBase category;
    public int x, y, width, height;
    public Player player;
    public Font renderer;

    public boolean drawOnLeft;
    public ItemStack bookStack;

    public CategoryWrapper(Book book, CategoryBase category, int x, int y, int width, int height, Player player, Font renderer, boolean drawOnLeft) {
        this.book = book;
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.player = player;
        this.renderer = renderer;
        this.drawOnLeft = drawOnLeft;
    }

    @Override
    public boolean canPlayerSee() {
        return category.canSee(player, book);
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, GuideBookScreen gui) {
        category.draw(graphics, book, x, y, width, height, mouseX, mouseY, gui, drawOnLeft);
    }

    @Override
    public void drawExtras(GuiGraphics graphics, int mouseX, int mouseY, GuideBookScreen gui) {
        category.drawExtras(graphics, book, x, y, width, height, mouseX, mouseY, gui, drawOnLeft);
    }

    @Override
    public boolean isMouseOnWrapper(double mouseX, double mouseY) {
        return GuiHelper.isMouseBetween(mouseX, mouseY, x, y, width, height);
    }

    @Override
    public void onHoverOver(int mouseX, int mouseY) {
    }
}
