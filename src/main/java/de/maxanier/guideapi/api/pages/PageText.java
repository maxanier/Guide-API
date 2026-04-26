package de.maxanier.guideapi.api.pages;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.PageHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.FormattedText;

import java.util.Objects;


public class PageText extends Page {

    private final int yOffset;
    public FormattedText draw;

    /**
     * @param draw    - Text to draw. Checks for localization.
     * @param yOffset - How many pixels to offset the text on the Y value
     */
    public PageText(FormattedText draw, int yOffset) {
        this.draw = draw;
        this.yOffset = yOffset;
    }

    public PageText(FormattedText draw) {
        this(draw, 5);
    }

    @Override
    public void draw(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        PageHelper.drawFormattedText(graphics, guiLeft + 44, guiTop + 12 + yOffset, draw, book.getTextColor());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageText pageText)) return false;
        if (!super.equals(o)) return false;

        if (yOffset != pageText.yOffset) return false;
        return Objects.equals(draw, pageText.draw);
    }

    @Override
    public int hashCode() {
        int result = draw != null ? draw.hashCode() : 0;
        result = 31 * result + yOffset;
        return result;
    }
}
