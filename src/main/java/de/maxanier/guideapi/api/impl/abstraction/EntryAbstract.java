package de.maxanier.guideapi.api.impl.abstraction;

import com.google.common.collect.Lists;
import de.maxanier.guideapi.api.IPage;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.gui.BaseScreen;
import de.maxanier.guideapi.gui.CategoryScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Objects;

public abstract class EntryAbstract {

    public final List<IPage> pageList;
    public final Component name;

    public EntryAbstract(List<IPage> pageList, Component name) {
        this.pageList = pageList;
        this.name = name;
    }

    public EntryAbstract(Component name) {
        this(Lists.newArrayList(), name);
    }


    public void addPage(IPage page) {
        this.pageList.add(page);
    }

    public void addPageList(List<IPage> pages) {
        this.pageList.addAll(pages);
    }

    public abstract boolean canSee(Player player, ItemStack bookStack);

    @OnlyIn(Dist.CLIENT)
    public abstract void draw(GuiGraphics graphics, RegistryAccess registryAccess, Book book, CategoryAbstract category, int entryX, int entryY, int entryWidth, int entryHeight, int mouseX, int mouseY, BaseScreen guiBase, Font renderer);

    @OnlyIn(Dist.CLIENT)
    public abstract void drawExtras(GuiGraphics graphics, Book book, CategoryAbstract category, int entryX, int entryY, int entryWidth, int entryHeight, int mouseX, int mouseY, BaseScreen guiBase, Font renderer);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntryAbstract that = (EntryAbstract) o;
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

    @OnlyIn(Dist.CLIENT)
    public abstract void onInit(Book book, CategoryAbstract category, CategoryScreen guiCategory, Player player, ItemStack bookStack);

    @OnlyIn(Dist.CLIENT)
    public abstract void onLeftClicked(Book book, CategoryAbstract category, double mouseX, double mouseY, Player player, CategoryScreen guiCategory);

    @OnlyIn(Dist.CLIENT)
    public abstract void onRightClicked(Book book, CategoryAbstract category, double mouseX, double mouseY, Player player, CategoryScreen guiCategory);

    public void removePage(IPage page) {
        this.pageList.remove(page);
    }

    public void removePageList(List<IPage> pages) {
        this.pageList.removeAll(pages);
    }
}
