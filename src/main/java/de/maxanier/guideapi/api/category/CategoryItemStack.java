package de.maxanier.guideapi.api.category;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Objects;

public class CategoryItemStack extends Category {

    public ItemStack itemStack;

    public CategoryItemStack(Map<Identifier, EntryBase> entries, Component name, ItemStack stack) {
        super(entries, name);
        this.itemStack = stack;
    }

    public CategoryItemStack(Component name, ItemStack stack) {
        super(name);
        this.itemStack = stack;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX, int mouseY, GuideBookScreen screen, boolean drawOnLeft) {
        GuiHelper.drawScaledItemStack(graphics, this.itemStack, categoryX, categoryY, 1.5F);
    }

    @Override
    public void drawExtras(GuiGraphicsExtractor graphics, Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX, int mouseY, GuideBookScreen screen, boolean drawOnLeft) {
        if (canSee(screen.player(), book) && GuiHelper.isMouseBetween(mouseX, mouseY, categoryX, categoryY, categoryWidth, categoryHeight)) {
            graphics.setTooltipForNextFrame(
                    Minecraft.getInstance().font,
                    this.getTooltip(),
                    itemStack.getTooltipImage(),
                    itemStack,
                    mouseX,
                    mouseY,
                    itemStack.get(DataComponents.TOOLTIP_STYLE)
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryItemStack that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(itemStack, that.itemStack);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (itemStack != null ? itemStack.hashCode() : 0);
        return result;
    }
}
