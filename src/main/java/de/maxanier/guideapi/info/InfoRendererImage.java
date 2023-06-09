package de.maxanier.guideapi.info;

import de.maxanier.guideapi.api.IInfoRenderer;
import de.maxanier.guideapi.api.impl.Book;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class InfoRendererImage implements IInfoRenderer {

    private final ResourceLocation image;
    private final int imageX;
    private final int imageY;
    private final int imageWidth;
    private final int imageHeight;

    public InfoRendererImage(ResourceLocation image, int imageX, int imageY, int imageWidth, int imageHeight) {
        this.image = image;
        this.imageX = imageX;
        this.imageY = imageY;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    public void drawInformation(GuiGraphics graphics, Book book, Level world, BlockPos pos, BlockState state, HitResult rayTrace, Player player) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(image, Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 20, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 - imageHeight / 2, imageX, imageY, imageWidth, imageHeight, imageWidth, imageHeight);
    }
}
