package de.maxanier.guideapi.button;

import com.mojang.blaze3d.systems.RenderSystem;
import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.button.ButtonGuideAPI;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.gui.BaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ButtonBack extends ButtonGuideAPI {

    public static final ResourceLocation TEXTURE = new ResourceLocation(GuideMod.ID, "textures/gui/book_colored.png");

    public ButtonBack(int widthIn, int heightIn, Button.OnPress onPress, BaseScreen guiBase) {
        super(widthIn, heightIn, 18, 10, Component.translatable("guideapi.button.back"), onPress, guiBase);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            RenderSystem.enableBlend();
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (GuiHelper.isMouseBetween(mouseX, mouseY, getX(), getY(), width, height)) { //x,y,width,height
                graphics.blit(TEXTURE, getX(), getY() + 1, 70, 201, 18, 10);
                graphics.renderTooltip(Minecraft.getInstance().font, getHoveringText(), Optional.empty(), mouseX, mouseY);
            } else {
                graphics.blit(TEXTURE, getX(), getY(), 94, 201, 18, 10);
            }
            RenderSystem.disableBlend();
        }
    }
}
