package de.maxanier.guideapi.api.pages;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public class PageImage extends Page {

    public Identifier image;
    protected int textureWidth, textureHeight;
    protected boolean scale;

    /**
     * @param image         Location of the image
     * @param textureWidth  The width of the image file
     * @param textureHeight The height of the image file
     * @param scale Whether the image should be scaled to fill the page
     */
    public PageImage(Identifier image, int textureWidth, int textureHeight, boolean scale) {
        this.image = image;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.scale = scale;
    }

    /**
     *
     * @param image Location of a 64x64 image
     */
    public PageImage(Identifier image) {
        this(image, 64, 64, false);
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {

        int width = textureWidth;
        int height = textureHeight;
        if (scale) {
            float factor1 = screen.pageWidth() / (float) textureWidth;
            float factor2 = screen.pageHeight() / (float) textureHeight;
            float factor = Math.min(factor1, factor2);
            width = (int) (width * factor);
            height = (int) (height * factor);
        }
        int x = pageLeft + (screen.pageWidth() - width) / 2;
        int y = pageTop + (screen.pageHeight() - height) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, image, x, y, 0, 0, width, height, width, height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageImage pageImage)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(image, pageImage.image);
    }

    @Override
    public int hashCode() {
        return image != null ? image.hashCode() : 0;
    }
}
