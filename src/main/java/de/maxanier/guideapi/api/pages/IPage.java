package de.maxanier.guideapi.api.pages;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;

public interface IPage {

    default boolean canSee(Book book, CategoryBase category, EntryBase entry, Player player) {
        return true;
    }

    default void draw(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {

    }

    default void drawExtras(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {

    }

    default void onClose() {

    }

    default void onInit(RegistryAccess registryAccess, Book book, CategoryBase category, EntryBase entry, Player player) {

    }

    default void onLeftClicked(Book book, CategoryBase category, EntryBase entry, double mouseX, double mouseY, Player player, GuideBookScreen screen) {

    }

    default void onRightClicked(Book book, CategoryBase category, EntryBase entry, double mouseX, double mouseY, Player player, GuideBookScreen screen) {

    }
}
