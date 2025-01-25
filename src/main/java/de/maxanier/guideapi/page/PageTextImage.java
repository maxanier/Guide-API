package de.maxanier.guideapi.page;

import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.Page;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.gui.BaseScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public class PageTextImage extends Page {

    public PageText pageText;
    public Identifier image;
    public boolean drawAtTop;
    protected int textureWidth, textureHeight;

    /**
     * @param draw      - Localized text to draw
     * @param image     - Image to draw
     * @param drawAtTop - Draw Image at top and text at bottom. False reverses this.
     * @param textureWidth Width of the image file
     * @param textureHeight Height of the image file
     */
    public PageTextImage(FormattedText draw, Identifier image, boolean drawAtTop, int textureWidth, int textureHeight) {
        this.pageText = new PageText(draw, drawAtTop ? 0 : 100);
        this.image = image;
        this.drawAtTop = drawAtTop;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }


    /**
     * @param draw      Text to render
     * @param image     Location of a 64x64 image
     * @param drawAtTop Whether the text should be at the top of the page
     */
    public PageTextImage(FormattedText draw, Identifier image, boolean drawAtTop) {
        this(draw, image, drawAtTop, 64, 64);
    }

    @Override
    public void draw(GuiGraphics graphics, RegistryAccess registryAccess, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj) {
        int x = guiLeft + (guiBase.xSize - textureWidth) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, image, x, guiTop + (drawAtTop ? 60 : 12), 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        pageText.draw(graphics, registryAccess, book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageTextImage that)) return false;
        if (!super.equals(o)) return false;

        if (drawAtTop != that.drawAtTop) return false;
        if (!Objects.equals(pageText, that.pageText)) return false;
        return Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        int result = pageText != null ? pageText.hashCode() : 0;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (drawAtTop ? 1 : 0);
        return result;
    }
}
