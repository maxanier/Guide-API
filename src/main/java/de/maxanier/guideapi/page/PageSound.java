package de.maxanier.guideapi.page;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.IPage;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.Page;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.gui.BaseScreen;
import de.maxanier.guideapi.gui.EntryScreen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PageSound extends Page {

    public IPage pageToEmulate;
    public SoundEvent sound;

    /**
     * @param pageToEmulate - Which page to use as a base
     * @param sound         - Sound to play
     */
    public PageSound(IPage pageToEmulate, SoundEvent sound) {
        this.pageToEmulate = pageToEmulate;
        this.sound = sound;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(MatrixStack stack, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, FontRenderer fontRendererObj) {
        pageToEmulate.draw(stack, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawExtras(MatrixStack stack, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, FontRenderer fontRendererObj) {
        pageToEmulate.drawExtras(stack, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
    }

    @Override
    public boolean canSee(Book book, CategoryAbstract category, EntryAbstract entry, PlayerEntity player, ItemStack bookStack, EntryScreen guiEntry) {
        return pageToEmulate.canSee(book, category, entry, player, bookStack, guiEntry);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onLeftClicked(Book book, CategoryAbstract category, EntryAbstract entry, double mouseX, double mouseY, PlayerEntity player, EntryScreen guiEntry) {
        GuideMod.PROXY.playSound(sound);
        pageToEmulate.onLeftClicked(book, category, entry, mouseX, mouseY, player, guiEntry);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onRightClicked(Book book, CategoryAbstract category, EntryAbstract entry, double mouseX, double mouseY, PlayerEntity player, EntryScreen guiEntry) {
        pageToEmulate.onRightClicked(book, category, entry, mouseX, mouseY, player, guiEntry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageSound)) return false;
        if (!super.equals(o)) return false;

        PageSound pageSound = (PageSound) o;

        if (pageToEmulate != null ? !pageToEmulate.equals(pageSound.pageToEmulate) : pageSound.pageToEmulate != null)
            return false;
        return sound != null ? sound.equals(pageSound.sound) : pageSound.sound == null;
    }

    @Override
    public int hashCode() {
        int result = pageToEmulate != null ? pageToEmulate.hashCode() : 0;
        result = 31 * result + (sound != null ? sound.hashCode() : 0);
        return result;
    }
}
