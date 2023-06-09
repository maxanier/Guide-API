package de.maxanier.guideapi.button;

import com.mojang.blaze3d.systems.RenderSystem;
import de.maxanier.guideapi.api.SubTexture;
import de.maxanier.guideapi.api.button.ButtonGuideAPI;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.gui.BaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ButtonSearch extends ButtonGuideAPI {

    public ButtonSearch(int widthIn, int heightIn, Button.OnPress onPress, BaseScreen guiBase) {
        super(widthIn, heightIn, 15, 15, Component.translatable("guideapi.button.search"), onPress, guiBase);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (GuiHelper.isMouseBetween(mouseX, mouseY, getX(), getY(), width, height)) { //x,y,width,height
                SubTexture.MAGNIFYING_GLASS.draw(graphics, getX(), getY() + 1);
                graphics.renderTooltip(Minecraft.getInstance().font, getHoveringText(), Optional.empty(), mouseX, mouseY);
            } else {
                SubTexture.MAGNIFYING_GLASS.draw(graphics, getX(), getY());
            }
            RenderSystem.disableBlend();
        }
    }

}
