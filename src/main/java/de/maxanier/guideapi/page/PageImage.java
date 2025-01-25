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
import net.minecraft.resources.Identifier;

import java.util.Objects;

public class PageImage extends Page {

    public Identifier image;
    protected int textureWidth, textureHeight;

    /**
     * @param image Location of the image
     * @param textureWidth The width of the image file
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
    public void draw(GuiGraphics graphics, RegistryAccess registryAccess, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj) {
        int x = guiLeft + (guiBase.xSize - textureWidth) / 2;
        int y = guiTop + (guiBase.ySize - textureHeight) / 2;
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
