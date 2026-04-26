package de.maxanier.guideapi.api.pages;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

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
    public boolean canSee(Book book, CategoryBase category, EntryBase entry, Player player) {
        return pageToEmulate.canSee(book, category, entry, player);
    }

    @Override
    public void draw(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        pageToEmulate.draw(graphics, book, category, entry, guiLeft, guiTop, mouseX, mouseY, screen, fontRendererObj);
    }

    @Override
    public void drawExtras(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        pageToEmulate.drawExtras(graphics, book, category, entry, guiLeft, guiTop, mouseX, mouseY, screen, fontRendererObj);
    }

    @Override
    public void onInit(RegistryAccess registryAccess, Book book, CategoryBase category, EntryBase entry, Player player) {
        pageToEmulate.onInit(registryAccess, book, category, entry, player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageSound pageSound)) return false;
        if (!super.equals(o)) return false;

        if (!Objects.equals(pageToEmulate, pageSound.pageToEmulate))
            return false;
        return Objects.equals(sound, pageSound.sound);
    }

    @Override
    public int hashCode() {
        int result = pageToEmulate != null ? pageToEmulate.hashCode() : 0;
        result = 31 * result + (sound != null ? sound.hashCode() : 0);
        return result;
    }

    @Override
    public void onLeftClicked(Book book, CategoryBase category, EntryBase entry, double mouseX, double mouseY, Player player, GuideBookScreen screen) {
        GuideMod.PROXY.playSound(sound);
        pageToEmulate.onLeftClicked(book, category, entry, mouseX, mouseY, player, screen);
    }

    @Override
    public void onRightClicked(Book book, CategoryBase category, EntryBase entry, double mouseX, double mouseY, Player player, GuideBookScreen screen) {
        pageToEmulate.onRightClicked(book, category, entry, mouseX, mouseY, player, screen);
    }
}
