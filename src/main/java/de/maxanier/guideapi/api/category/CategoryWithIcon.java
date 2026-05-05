package de.maxanier.guideapi.api.category;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Untested/Unused
 */
@Deprecated(forRemoval = true)
public class CategoryWithIcon extends Category {

    public Identifier image;

    public CategoryWithIcon(Map<Identifier, EntryBase> entries, Component name, Identifier image) {
        super(entries, name);
        this.image = image;
    }

    public CategoryWithIcon(Component name, Identifier image) {
        super(name);
        this.image = image;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX, int mouseY, GuideBookScreen guiBase, boolean drawOnLeft) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.image, categoryX, categoryY, 0, 0, 48, 48, 48, 48);
    }

    @Override
    public void drawExtras(GuiGraphicsExtractor graphics, Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX, int mouseY, GuideBookScreen guiBase, boolean drawOnLeft) {
        if (canSee(guiBase.player(), book) && GuiHelper.isMouseBetween(mouseX, mouseY, categoryX, categoryY, categoryWidth, categoryHeight)) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font,
                    this.getTooltip(),
                    Optional.empty(),
                    mouseX,
                    mouseY,
                    null
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryWithIcon that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.image != null ? this.image.hashCode() : 0);
        return result;
    }
}
