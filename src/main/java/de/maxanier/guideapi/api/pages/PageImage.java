package de.maxanier.guideapi.api.pages;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public class PageImage extends Page {

    public Identifier image;
    protected int textureWidth, textureHeight;

    /**
     * @param image         Location of the image
     * @param textureWidth  The width of the image file
     * @param textureHeight The height of the image file
     */
    public PageImage(Identifier image, int textureWidth, int textureHeight) {
        this.image = image;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    /**
     *
     * @param image Location of a 64x64 image
     */
    public PageImage(Identifier image) {
        this(image, 64, 64);
    }

    @Override
    public void draw(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        int x = guiLeft + (screen.xSize() - textureWidth) / 2;
        int y = guiTop + (screen.ySize() - textureHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, image, x, y, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
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
