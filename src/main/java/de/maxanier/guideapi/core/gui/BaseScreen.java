package de.maxanier.guideapi.core.gui;

import com.mojang.blaze3d.platform.InputConstants;
import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.util.GuiHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public abstract class BaseScreen extends Screen implements GuideBookScreen {

    protected final Book book;
    protected final int screenWidth = 197;
    protected final int screenHeight = 181;
    private final int pageWidth = 167;
    private final int pageHeight = 145;
    private final int pageXOffset = 39;
    private final int pageYOffset = 13;
    private final int backgroundXOffset = 25;
    private final int backgroundYOffset = 0;
    private final Identifier outlineTexture;
    private final Identifier pageTexture;
    private final Player player;
    private int screenTop, screenLeft;
    private int pageTop, pageLeft;
    private NavigationButton btnNext;
    private NavigationButton btnPrev;
    @Nullable
    private NavigationButton btnBack;
    private int currentPage;

    public BaseScreen(Component title, Book book, Player player) {
        super(title);
        this.book = book;
        this.pageTexture = book.getPageTexture();
        this.outlineTexture = book.getOutlineTexture();

        this.player = player;
    }

    public int currentPage() {
        return currentPage;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if ((keyCode == InputConstants.KEY_UP || keyCode == InputConstants.KEY_RIGHT)) {
            this.btnNext.onPress(event);
            return true;
        } else if ((keyCode == InputConstants.KEY_DOWN || keyCode == InputConstants.KEY_LEFT)) {
            this.btnPrev.onPress(event);
            return true;
        } else if (keyCode == InputConstants.KEY_BACKSPACE || keyCode == this.minecraft.options.keyUse.getKey().getValue()) {
            if (btnBack != null) {
                btnBack.onPress(event);
                return true;
            }
        } else if ((event.key() == InputConstants.KEY_ESCAPE || event.key() == this.minecraft.options.keyInventory.getKey().getValue())) { //Close not only on escape but also on pressing the inventory key
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public int pageHeight() {
        return pageHeight;
    }

    @Override
    public int pageLeft() {
        return pageLeft;
    }

    @Override
    public int pageTop() {
        return pageTop;
    }

    @Override
    public int pageWidth() {
        return pageWidth;
    }

    @Override
    public int pageXCenter() {
        return pageLeft + pageWidth / 2;
    }

    @Override
    public int pageYCenter() {
        return pageTop + pageHeight / 2;
    }

    @Override
    public Player player() {
        return player;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        if (getPageCount() > 1) {
            GuiHelper.drawCenteredStringWithoutShadow(graphics, font, Component.literal(String.format("%d/%d", currentPage() + 1, getPageCount())), pageLeft() + pageWidth / 2, pageTop() + pageHeight() + 2, book.getTextColor());
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, pageTexture, screenLeft, screenTop, backgroundXOffset, backgroundYOffset, screenWidth, screenHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, outlineTexture, screenLeft, screenTop, backgroundXOffset, backgroundYOffset, screenWidth, screenHeight, 256, 256, book.getThemeColor());
    }

    public void setPage(int pageNum) {
        int i = Mth.clamp(pageNum, 0, getPageCount() - 1);
        if (i != this.currentPage) {
            this.currentPage = i;
            this.updateButtonVisibility();
        }
    }

    /**
     * Must call at the end of init after page count is init (or call {@link BaseScreen#updateButtonVisibility()} afterwards
     *
     * @param back   Whether to add a back button
     * @param search Whether to add a search button
     */
    protected void addButtons(boolean back, boolean search) {
        addRenderableWidget(btnNext = NavigationButton.create(book.getPageTexture(), pageLeft() + pageWidth() - 25, pageTop() + pageHeight() + 2, NavigationButton.TYPE.NEXT, btn -> pageNext()));
        addRenderableWidget(btnPrev = NavigationButton.create(book.getPageTexture(), pageLeft() + 5, pageTop() + pageHeight() + 2, NavigationButton.TYPE.PREV, btn -> pagePrev()));
        if (back) {
            addRenderableWidget(btnBack = NavigationButton.create(book.getPageTexture(), pageLeft(), pageTop() - 14, NavigationButton.TYPE.BACK, btn -> goBack()));
        }
        if (search) {
            addRenderableWidget(NavigationButton.create(book.getPageTexture(), screenLeft - 15, screenTop, NavigationButton.TYPE.SEARCH, btn -> startSearch()));
        }
        updateButtonVisibility();
    }

    protected void goBack() {

    }

    @MustBeInvokedByOverriders
    @Override
    protected void init() {
        screenLeft = (this.width - this.screenWidth) / 2;
        screenTop = (this.height - this.screenHeight) / 2;
        this.pageLeft = screenLeft + pageXOffset - backgroundXOffset;
        this.pageTop = screenTop + pageYOffset - backgroundYOffset;
    }

    protected void pageNext() {
        if (currentPage < getPageCount() - 1) {
            currentPage++;
        }
        this.updateButtonVisibility();
    }

    protected void pagePrev() {
        if (currentPage > 0) {
            currentPage--;
        }
        this.updateButtonVisibility();
    }

    protected int screenLeft() {
        return screenLeft;
    }

    protected int screenTop() {
        return screenTop;
    }

    protected void startSearch() {

    }

    private void updateButtonVisibility() {
        btnNext.visible = currentPage < getPageCount() - 1;
        btnPrev.visible = currentPage > 0;

    }
}
