package de.maxanier.guideapi.api.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;



public class GuiHelper {
    private static final ItemRenderer render = Minecraft.getInstance().getItemRenderer();

    /**
     * @param mouseX - Position of the mouse on the x-axiq
     * @param mouseY - Position of the mouse on the y-axis
     * @param x      - Starting x for the rectangle
     * @param y      - Starting y for the rectangle
     * @param width  - Width of the rectangle
     * @param height - Height of the rectangle
     * @return whether or not the mouse is in the rectangle
     */
    public static boolean isMouseBetween(double mouseX, double mouseY, int x, int y, int width, int height) {
        int xSize = x + width;
        int ySize = y + height;
        return (mouseX >= x && mouseX <= xSize && mouseY >= y && mouseY <= ySize);
    }

    /**
     * MatrixStack isn't used yet as vanilla ItemRenderer does not use it yet.
     *
     * @param stack - The itemstack to be drawn
     * @param x     - The position on the x-axis to draw the itemstack
     * @param y     - The position on the y-axis to draw the itemstack
     */
    public static void drawItemStack(MatrixStack mStack, ItemStack stack, int x, int y) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableStandardItemLighting();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableDepthTest();
        render.renderItemAndEffectIntoGUI(stack, x, y);
        render.renderItemOverlayIntoGUI(Minecraft.getInstance().fontRenderer, stack, x, y, null);
        RenderHelper.disableStandardItemLighting();
        RenderSystem.popMatrix();
        RenderSystem.disableLighting();
    }

    /**
     * MatrixStack isn't used yet as vanilla ItemRenderer does not use it yet.
     * @param stack - The itemstack to be drawn
     * @param x     - The position on the x-axis to draw the itemstack
     * @param y     - The position on the y-axis to draw the itemstack
     * @param scale - The scale with which to draw the itemstack
     */
    public static void drawScaledItemStack(MatrixStack mStack, ItemStack stack, int x, int y, float scale) {
        RenderSystem.pushMatrix();
        RenderSystem.scalef(scale, scale, 1f);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableStandardItemLighting();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableDepthTest();//enableDepth?
        render.renderItemAndEffectIntoGUI(stack, (int) (x / scale), (int) (y / scale));
        RenderHelper.disableStandardItemLighting();
        RenderSystem.popMatrix();
    }

    /**
     * MatrixStack isn't used yet as vanilla ItemRenderer does not use it yet.
     * @param x      - The position on the x-axis to draw the icon
     * @param y      - The position on the y-axis to draw the icon
     * @param width  - The width of the icon
     * @param height - The height of the icon
     * @param zLevel -
     */
    public static void drawIconWithoutColor(MatrixStack mStack, int x, int y, int width, int height, float zLevel) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableStandardItemLighting();
        RenderSystem.disableLighting();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableDepthTest();
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        tessellator.getBuffer().pos(x, y + height, zLevel).tex(0f, 1f).endVertex();
        tessellator.getBuffer().pos(x + width, y + height, zLevel).tex(1f, 1f).endVertex();
        tessellator.getBuffer().pos(x + width, y, zLevel).tex(1f, 0f).endVertex();
        tessellator.getBuffer().pos(x, y, zLevel).tex(0f, 0f).endVertex();
        tessellator.draw();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.popMatrix();
    }

    /**
     * MatrixStack isn't used yet as vanilla ItemRenderer does not use it yet.
     * @param x      - The position on the x-axis to draw the icon
     * @param y      - The position on the y-axis to draw the icon
     * @param width  - The width of the icon
     * @param height - The height of the icon
     * @param zLevel -
     * @param color  - The color the icon will have
     */
    public static void drawIconWithColor(MatrixStack mStack, int x, int y, int width, int height, float zLevel, Color color) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableStandardItemLighting();
        RenderSystem.disableLighting();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableDepthTest();
        RenderSystem.color4f((float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F, (float) color.getAlpha() / 255F);
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        tessellator.getBuffer().pos(x, y + height, zLevel).tex(0f, 1f).endVertex();
        tessellator.getBuffer().pos(x + width, y + height, zLevel).tex(1f, 1f).endVertex();
        tessellator.getBuffer().pos(x + width, y, zLevel).tex(1f, 0f).endVertex();
        tessellator.getBuffer().pos(x, y, zLevel).tex(0f, 0f).endVertex();
        tessellator.draw();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.popMatrix();
    }

    /**
     * MatrixStack isn't used yet as vanilla ItemRenderer does not use it yet.
     * @param x      - The position on the x-axis to draw the icon
     * @param y      - The position on the y-axis to draw the icon
     * @param width  - The width of the icon
     * @param height - The height of the icon
     * @param zLevel -
     */
    public static void drawSizedIconWithoutColor(MatrixStack mStack, int x, int y, int width, int height, float zLevel) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.scalef(0.5f, 0.5f, 0.5f);
        RenderSystem.translatef(x, y, zLevel);
        RenderHelper.enableStandardItemLighting();
        RenderSystem.disableLighting();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableDepthTest();
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        tessellator.getBuffer().pos(x, y + height, zLevel).tex(0f, 1f).endVertex();
        tessellator.getBuffer().pos(x + width, y + height, zLevel).tex(1f, 1).endVertex();
        tessellator.getBuffer().pos(x + width, y, zLevel).tex(1, 0).endVertex();
        tessellator.getBuffer().pos(x, y, zLevel).tex(0, 0).endVertex();
        tessellator.draw();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.popMatrix();
    }

    /**
     * MatrixStack isn't used yet as vanilla ItemRenderer does not use it yet.
     * @param x      - The position on the x-axis to draw the icon
     * @param y      - The position on the y-axis to draw the icon
     * @param width  - The width of the icon
     * @param height - The height of the icon
     * @param color  - The color the icon will have
     */
    public static void drawSizedIconWithColor(MatrixStack mStack, int x, int y, int width, int height, float zLevel, Color color) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.scalef(0.5f, 0.5f, 0.5f);
        RenderSystem.color4f((float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F, (float) color.getAlpha() / 255F);
        RenderSystem.translatef(x, y, zLevel);
        RenderHelper.enableStandardItemLighting();
        RenderSystem.disableLighting();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableDepthTest();
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        tessellator.getBuffer().pos(x, y + height, zLevel).tex(0, 1).endVertex();
        tessellator.getBuffer().pos(x + width, y + height, zLevel).tex(1, 1).endVertex();
        tessellator.getBuffer().pos(x + width, y, zLevel).tex(1, 0).endVertex();
        tessellator.getBuffer().pos(x, y, zLevel).tex(0, 0).endVertex();
        tessellator.draw();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.popMatrix();
    }

    @SuppressWarnings("unchecked")
    public static List<ITextComponent> getTooltip(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        List<ITextComponent> list = stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL);
        for (int k = 0; k < list.size(); ++k) {
            ITextComponent c = list.get(k);
            if (c instanceof IFormattableTextComponent) {
                if (k == 0) {
                    ((IFormattableTextComponent) c).mergeStyle(stack.getRarity().color); //applyTextComponent
                } else {
                    ((IFormattableTextComponent) c).mergeStyle(TextFormatting.GRAY);
                }
            }

        }
        return list;
    }
}
