package de.maxanier.guideapi.api.world;

import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class InfoOverlayText implements IInfoOverlay {

    private final ItemStack itemStack;
    private final Component description;
    private boolean tiny;
    private int yOffset;

    public InfoOverlayText(ItemStack stack, Component description) {
        this.itemStack = stack;
        this.description = description;
    }

    @Override
    public void drawInformation(GuiGraphicsExtractor graphics, Book book, Level world, BlockPos pos, BlockState state, HitResult rayTrace, Player player) {
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

        graphics.textWithWordWrap(fontRenderer, description, x * scaleMulti, y * scaleMulti, 100 * scaleMulti, -1);

        if (tiny)
            graphics.pose().popMatrix();
    }

    public InfoOverlayText setOffsetY(int yOffset) {
        this.yOffset = yOffset;
        return this;
    }

    public InfoOverlayText setTiny(boolean tiny) {
        this.tiny = tiny;
        return this;
    }
}
