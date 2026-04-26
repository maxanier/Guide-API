package de.maxanier.guideapi.api.entry;

import com.google.common.collect.Lists;
import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.pages.IPage;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;

public abstract class EntryBase {

    public final List<IPage> pageList;
    public final Component name;

    public EntryBase(List<IPage> pageList, Component name) {
        this.pageList = pageList;
        this.name = name;
    }

    public EntryBase(Component name) {
        this(Lists.newArrayList(), name);
    }


    public void addPage(IPage page) {
        this.pageList.add(page);
    }

    public void addPageList(List<IPage> pages) {
        this.pageList.addAll(pages);
    }

    public abstract boolean canSee(Player player, Book bookStack);

    public abstract void draw(GuiGraphics graphics, Book book, CategoryBase category, int entryX, int entryY, int entryWidth, int entryHeight, int mouseX, int mouseY, GuideBookScreen screen, Font renderer);

    public abstract void drawExtras(GuiGraphics graphics, Book book, CategoryBase category, int entryX, int entryY, int entryWidth, int entryHeight, int mouseX, int mouseY, GuideBookScreen screen, Font renderer);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntryBase that = (EntryBase) o;
        if (!Objects.equals(pageList, that.pageList)) return false;
        return Objects.equals(name, that.name);
    }

    public Component getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int result = pageList != null ? pageList.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public abstract void onInit(RegistryAccess registryAccess, Book book, CategoryBase category, Player player);

    public abstract void onLeftClicked(Book book, CategoryBase category, double mouseX, double mouseY, Player player);

    public abstract void onRightClicked(Book book, CategoryBase category, double mouseX, double mouseY, Player player);

    public void removePage(IPage page) {
        this.pageList.remove(page);
    }

    public void removePageList(List<IPage> pages) {
        this.pageList.removeAll(pages);
    }
}
