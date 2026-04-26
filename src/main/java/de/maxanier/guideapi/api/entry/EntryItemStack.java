package de.maxanier.guideapi.api.entry;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.pages.IPage;
import de.maxanier.guideapi.api.util.GuiHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public class EntryItemStack extends Entry {

    public ItemStack itemStack;

    public EntryItemStack(List<IPage> pageList, Component name, ItemStack stack) {
        super(pageList, name);
        this.itemStack = stack;
    }


    public EntryItemStack(Component name, ItemStack stack) {
        super(name);
        this.itemStack = stack;
    }

    @Override
    public void drawExtras(GuiGraphics graphics, Book book, CategoryBase category, int entryX, int entryY, int entryWidth, int entryHeight, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        if (itemStack != null)
            GuiHelper.drawScaledItemStack(graphics, itemStack, entryX + 2, entryY, 0.5f);

        super.drawExtras(graphics, book, category, entryX, entryY, entryWidth, entryHeight, mouseX, mouseY, screen, fontRendererObj);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntryItemStack that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(itemStack, that.itemStack);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (itemStack != null ? itemStack.hashCode() : 0);
        return result;
    }
}
