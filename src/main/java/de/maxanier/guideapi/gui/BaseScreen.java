package de.maxanier.guideapi.gui;

import com.mojang.blaze3d.platform.InputConstants;
import de.maxanier.guideapi.api.impl.Book;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;


public class BaseScreen extends Screen {

    public int guiLeft, guiTop;
    public int xSize = 245;
    public int ySize = 192;

    protected final Book book;
    private final Identifier outlineTexture;
    private final Identifier pageTexture;

    public Player player;
    public ItemStack bookStack;

    public BaseScreen(Component title, Book book, Player player, ItemStack bookStack) {
        super(title);
        this.book = book;
        this.pageTexture = book.getPageTexture();
        this.outlineTexture = book.getOutlineTexture();

        this.player = player;
        this.bookStack = bookStack;
    }

    @Override
    protected void init() {
        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;
    }


    public void drawCenteredStringWithoutShadow(GuiGraphics graphics, Font fontRendererObj, Component string, int x, int y, int color) {
        graphics.drawString(fontRendererObj, string, x - fontRendererObj.width(string) / 2, y, color, false);
    }

    public void drawCenteredStringWithoutShadow(GuiGraphics graphics, Font fontRendererObj, Component string, int x, int y) {
        drawCenteredStringWithoutShadow(graphics, fontRendererObj, string, x, y, book.getTextColor());
    }

    @Override
    public void renderBackground(@NonNull GuiGraphics graphics, int p_296491_, int p_294260_, float p_294869_) {
        super.renderBackground(graphics, p_296491_, p_294260_, p_294869_);
        graphics.blit(RenderPipelines.GUI_TEXTURED, pageTexture, guiLeft, guiTop, 0, 0, xSize, ySize, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, outlineTexture, guiLeft, guiTop, 0.0F, 0.0F, xSize, ySize, 256, 256, book.getThemeColor());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        //Close not only on escape but also on pressing the inventory key
        if ((event.key() == InputConstants.KEY_ESCAPE || event.key() == this.minecraft.options.keyInventory.getKey().getValue())) {
            this.onClose();
            this.minecraft.setWindowActive(true);
            return true;
        } else {
            return super.keyPressed(event);
        }
    }
}
