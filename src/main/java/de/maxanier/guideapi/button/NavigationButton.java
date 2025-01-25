package de.maxanier.guideapi.button;

import de.maxanier.guideapi.GuideMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class NavigationButton extends Button {

    private static final Component TEXT_NEXT = Component.translatable("guideapi.button.next");
    private static final Component TEXT_PREV = Component.translatable("guideapi.button.prev");
    private static final Component TEXT_BACK = Component.translatable("guideapi.button.back");
    private static final Component TEXT_SEARCH = Component.translatable("guideapi.button.search");

    /**
     *
     * @param atlasTexture The book gui texture containing the button symbols at the correct positions
     * @param x            x placement
     * @param y            y placement
     * @param type         Button type
     * @param onPress      Press function
     */
    public static NavigationButton create(Identifier atlasTexture, int x, int y, TYPE type, OnPress onPress) {
        return switch (type) {
            case NEXT -> new NavigationButton(atlasTexture, 24, 47, 201, 201, x, y, 18, 10, TEXT_NEXT, onPress);
            case PREV -> new NavigationButton(atlasTexture, 24, 47, 214, 214,x, y, 18, 10, TEXT_PREV, onPress);
            case BACK -> new NavigationButton(atlasTexture, 94, 70, 201, 201,x, y,18, 10, TEXT_BACK, onPress);
            case SEARCH -> new NavigationButton(atlasTexture, 0, 0, 241, 241, x, y, 15, 15, TEXT_SEARCH, onPress);
        };
    }


    private final Identifier atlasTexture;
    private final int texture_u, texture_u_highlight, texture_v, texture_v_highlight;

    protected NavigationButton(Identifier texture, int texture_u, int texture_u_highlight, int texture_v, int texture_v_highlight, int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.atlasTexture = texture;
        this.texture_u = texture_u;
        this.texture_u_highlight = texture_u_highlight;
        this.texture_v = texture_v;
        this.texture_v_highlight = texture_v_highlight;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.atlasTexture, this.getX(), this.getY(), isHoveredOrFocused() ? this.texture_u_highlight : this.texture_u, isHoveredOrFocused() ? this.texture_v_highlight : this.texture_v, this.width, this.height, 256, 256);
    }

    public enum TYPE {
        PREV, NEXT, BACK, SEARCH
    }
}
