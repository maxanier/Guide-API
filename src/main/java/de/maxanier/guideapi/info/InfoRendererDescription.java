package de.maxanier.guideapi.info;

import de.maxanier.guideapi.api.IInfoRenderer;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import java.awt.*;
import java.util.List;

public class InfoRendererDescription implements IInfoRenderer {

    private final ItemStack itemStack;
    private final Component description;
    private boolean tiny;
    private int yOffset;

    public InfoRendererDescription(ItemStack stack, Component description) {
        this.itemStack = stack;
        this.description = description;
    }

    @Override
    public void drawInformation(GuiGraphics graphics, Book book, Level world, BlockPos pos, BlockState state, HitResult rayTrace, Player player) {
        if (tiny) {
            graphics.pose().pushMatrix();
            graphics.pose().scale(0.5F, 0.5F);
        }
        Font fontRenderer = Minecraft.getInstance().font;
        int scaleMulti = tiny ? 2 : 1;

        int x = (Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 20);
        int iconY = ((Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 - (tiny ? 20 : 30)) + yOffset);

        GuiHelper.drawItemStack(graphics, itemStack, x * scaleMulti, iconY * scaleMulti);

        int y = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 - 10;

        graphics.drawWordWrap(fontRenderer, description, x * scaleMulti, y * scaleMulti, 100 * scaleMulti, -1);

        if (tiny)
            graphics.pose().popMatrix();
    }

    public InfoRendererDescription setOffsetY(int yOffset) {
        this.yOffset = yOffset;
        return this;
    }

    public InfoRendererDescription setTiny(boolean tiny) {
        this.tiny = tiny;
        return this;
    }
}
