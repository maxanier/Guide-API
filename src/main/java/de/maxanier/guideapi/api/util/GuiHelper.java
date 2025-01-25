package de.maxanier.guideapi.api.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;

import java.util.List;


public class GuiHelper {

    /**
     * @param mouseX - Position of the mouse on the x-axiq
     * @param mouseY - Position of the mouse on the y-axis
     * @param x      - Starting x for the rectangle
     * @param y      - Starting y for the rectangle
     * @param width  - Width of the rectangle
     * @param height - Height of the rectangle
     * @return whether the mouse is in the rectangle
     */
    public static boolean isMouseBetween(double mouseX, double mouseY, int x, int y, int width, int height) {
        int xSize = x + width;
        int ySize = y + height;
        return (mouseX >= x && mouseX <= xSize && mouseY >= y && mouseY <= ySize);
    }

    /**
     *
     * @param x         - The position on the x-axis to draw the itemstack
     * @param y         - The position on the y-axis to draw the itemstack
     */
    public static void drawItemStack(GuiGraphics graphics, ItemStack stack, int x, int y) {
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y, null);
    }

    /**
     *
     * @param x         - The position on the x-axis to draw the itemstack
     * @param y         - The position on the y-axis to draw the itemstack
     * @param scale     - The scale with which to draw the itemstack
     */
    public static void drawScaledItemStack(GuiGraphics graphics, ItemStack stack, int x, int y, float scale) {
        var mStack = graphics.pose();
        mStack.pushMatrix();
        mStack.scale(scale, scale);
        graphics.renderItem(stack, (int) (x / scale), (int) (y / scale));
        mStack.popMatrix();
    }



    public static List<Component> getTooltip(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        List<Component> list = stack.getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, mc.options.advancedItemTooltips ? Default.ADVANCED : Default.NORMAL);
        for (int k = 0; k < list.size(); ++k) {
            Component c = list.get(k);
            if (c instanceof MutableComponent) {
                if (k == 0) {
                    ((MutableComponent) c).withStyle(stack.getRarity().getStyleModifier());
                } else {
                    ((MutableComponent) c).withStyle(ChatFormatting.GRAY);
                }
            }
        }
        return list;
    }
}
