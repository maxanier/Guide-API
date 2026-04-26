package de.maxanier.guideapi.api.entry;

import com.google.common.collect.Lists;
import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.pages.IPage;
import de.maxanier.guideapi.api.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

public class Entry extends EntryBase {

    /**
     * Set via {@link de.maxanier.guideapi.core.APISetter#setScreenFactories()}
     */
    private static EntryScreenFactory createEntryScreen;

    public Entry(List<IPage> pageList, Component name) {
        super(pageList, name);
    }

    public Entry(Component name) {
        super(name);
    }

    @Override
    public boolean canSee(Player player, Book bookStack) {
        return true;
    }

    @Override
    public void draw(GuiGraphics graphics, Book book, CategoryBase category, int entryX, int entryY, int entryWidth, int entryHeight, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {

        // Cutting code ripped from GuiButtonExt#drawButton(...)
        FormattedText entryName = getName();
        int strWidth = fontRendererObj.width(entryName);
        int ellipsisWidth = fontRendererObj.width("...");


        //Trim string if to long
        if (strWidth > entryWidth && strWidth > ellipsisWidth) {
            entryName = fontRendererObj.substrByWidth(entryName, entryWidth - ellipsisWidth);
            //Append dots
            entryName = FormattedText.composite(entryName, FormattedText.of("..."));
        }

        FormattedCharSequence entryNameRe = Language.getInstance().getVisualOrder(entryName);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, entryX, entryY, entryWidth, entryHeight)) {
            graphics.drawString(fontRendererObj, entryNameRe, entryX + 12, entryY + 1, book.getTextColorHighlighted(), false);
            graphics.drawString(fontRendererObj, entryNameRe, entryX + 12, entryY, 0x423EBC, false);
        } else {
            graphics.drawString(fontRendererObj, entryNameRe, entryX + 12, entryY, book.getTextColor(), false);
        }


    }

    @Override
    public void drawExtras(GuiGraphics graphics, Book book, CategoryBase category, int entryX, int entryY, int entryWidth, int entryHeight, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        // Cutting code ripped from GuiButtonExt#drawButton(...)
        int strWidth = fontRendererObj.width(getName());
        boolean cutString = strWidth > entryWidth && strWidth > fontRendererObj.width("...");

        if (GuiHelper.isMouseBetween(mouseX, mouseY, entryX, entryY, entryWidth, entryHeight) && cutString) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font,
                    Lists.newArrayList(getName()),
                    Optional.empty(),
                    entryX,
                    entryY + 12,
                    null
            );
        }


    }

    @Override
    public void onInit(RegistryAccess access, Book book, CategoryBase category, Player player) {
    }

    @Override
    public void onLeftClicked(Book book, CategoryBase category, double mouseX, double mouseY, Player player) {
        Minecraft.getInstance().setScreen(createEntryScreen.create(book, category, this, player));
    }

    @Override
    public void onRightClicked(Book book, CategoryBase category, double mouseX, double mouseY, Player player) {
    }

    @FunctionalInterface
    public interface EntryScreenFactory {
        Screen create(Book book, CategoryBase category, EntryBase entry, Player player);
    }
}