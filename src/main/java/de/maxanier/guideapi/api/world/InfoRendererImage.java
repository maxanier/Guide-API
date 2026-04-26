package de.maxanier.guideapi.api.world;

import com.mojang.blaze3d.platform.Window;
import de.maxanier.guideapi.api.book.Book;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

/**
 * @param imageX        The x position of the desired snippet
 * @param imageY        The y position of the desired snippet
 * @param imageWidth    The width of the desired snippet
 * @param imageHeight   The height of the desired snippet
 * @param textureWidth  Width of texture file
 * @param textureHeight Height of texture file
 */
public record InfoRendererImage(
        Identifier image,
        int imageX,
        int imageY,
        int imageWidth,
        int imageHeight,
        int textureWidth,
        int textureHeight
) implements IInfoRenderer {

    /***
     * @param image       A 256x256 texture
     */
    public InfoRendererImage(Identifier image, int imageX, int imageY, int imageWidth,
                             int imageHeight) {
        this(image, imageX, imageY, imageWidth, imageHeight, 256, 256);
    }

    /**
     *
     * @param image              A texture that should be rendered in its entirety
     * @param imageTextureWidth  Width of the texture
     * @param imageTextureHeight Height of the texture
     */
    public InfoRendererImage(Identifier image, int imageTextureWidth, int imageTextureHeight) {
        this(image, 0, 0, imageTextureWidth, imageTextureHeight, imageTextureWidth, imageTextureHeight);
    }

    @Override
    public void drawInformation(GuiGraphics graphics, Book book, Level world, BlockPos pos, BlockState state, HitResult rayTrace, Player player) {
        Window w = Minecraft.getInstance().getWindow();
        int x = w.getGuiScaledWidth() / 2 + 20;
        int y = w.getGuiScaledHeight() / 2 - imageHeight / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.image, x, y, imageX, imageY, imageWidth, imageHeight, textureWidth, textureHeight);
    }
}
