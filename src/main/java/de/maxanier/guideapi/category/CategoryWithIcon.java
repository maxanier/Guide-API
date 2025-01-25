package de.maxanier.guideapi.category;

import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.Category;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.gui.BaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Untested/Unused
 */
@Deprecated(forRemoval = true)
public class CategoryWithIcon extends Category {

    public Identifier image;

    public CategoryWithIcon(Map<Identifier, EntryAbstract> entries, Component name, Identifier image) {
        super(entries, name);
        this.image = image;
    }

    public CategoryWithIcon(Component name, Identifier image) {
        super(name);
        this.image = image;
    }

    @Override
    public void draw(GuiGraphics graphics, Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX, int mouseY, BaseScreen guiBase, boolean drawOnLeft) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.image, categoryX, categoryY, 0, 0, 48, 48, 48, 48);
        super.drawExtras(graphics, book, categoryX, categoryY, categoryWidth, categoryHeight, mouseX, mouseY, guiBase, drawOnLeft);
    }

    @Override
    public void drawExtras(GuiGraphics graphics, Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX, int mouseY, BaseScreen guiBase, boolean drawOnLeft) {
        if (canSee(guiBase.player, guiBase.bookStack) && GuiHelper.isMouseBetween(mouseX, mouseY, categoryX, categoryY, categoryWidth, categoryHeight)) {
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
