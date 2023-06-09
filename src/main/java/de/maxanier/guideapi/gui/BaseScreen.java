package de.maxanier.guideapi.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;


public class BaseScreen extends Screen {

    public int guiLeft, guiTop;
    public int xSize = 245;
    public int ySize = 192;
    public Player player;
    public ItemStack bookStack;

    public BaseScreen(Component title, Player player, ItemStack bookStack) {
        super(title);
        this.player = player;
        this.bookStack = bookStack;
    }

    public void drawCenteredStringWithoutShadow(GuiGraphics graphics, Font fontRendererObj, String string, int x, int y, int color) {
        graphics.drawString(fontRendererObj, string, x - fontRendererObj.width(string) / 2, y, color, false);
    }

    public void drawCenteredStringWithoutShadow(GuiGraphics graphics, Font fontRendererObj, FormattedCharSequence string, int x, int y, int color) {
        graphics.drawString(fontRendererObj, string, x - fontRendererObj.width(string) / 2, y, color, false);
    }

    public void drawCenteredStringWithoutShadow(GuiGraphics graphics, Font fontRendererObj, Component string, int x, int y, int color) {
        graphics.drawString(fontRendererObj, string, x - fontRendererObj.width(string) / 2, y, color, false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (minecraft != null && (p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE || p_keyPressed_1_ == this.minecraft.options.keyInventory.getKey().getValue())) {
            this.onClose();
            this.minecraft.setWindowActive(true);
            return true;
        } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }
}
