package de.maxanier.guideapi.core.gui;

import com.mojang.blaze3d.platform.InputConstants;
import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.util.GuiHelper;
import net.minecraft.client.gui.GuiGraphics;
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
    private final int xSize = 245;
    private final int ySize = 192;
    private final Identifier outlineTexture;
    private final Identifier pageTexture;
    private final Player player;
    private NavigationButton btnNext;
    private NavigationButton btnPrev;
    @Nullable
    private NavigationButton btnBack;
    private int currentPage;
    private int guiLeft;
    private int guiTop;

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
    public int guiLeft() {
        return guiLeft;
    }

    @Override
    public int guiTop() {
        return guiTop;
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
            this.minecraft.setWindowActive(true);
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public Player player() {
        return player;
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (getPageCount() > 1) {
            GuiHelper.drawCenteredStringWithoutShadow(graphics, font, Component.literal(String.format("%d/%d", currentPage() + 1, getPageCount())), guiLeft() + xSize() / 2, guiTop() + 5 * ySize() / 6, book.getTextColor());
        }
    }

    @Override
    public void renderBackground(@NonNull GuiGraphics graphics, int p_296491_, int p_294260_, float p_294869_) {
        super.renderBackground(graphics, p_296491_, p_294260_, p_294869_);
        graphics.blit(RenderPipelines.GUI_TEXTURED, pageTexture, guiLeft(), guiTop(), 0, 0, xSize(), ySize(), 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, outlineTexture, guiLeft(), guiTop(), 0.0F, 0.0F, xSize(), ySize(), 256, 256, book.getThemeColor());
    }

    public void setPage(int pageNum) {
        int i = Mth.clamp(pageNum, 0, getPageCount() - 1);
        if (i != this.currentPage) {
            this.currentPage = i;
            this.updateButtonVisibility();
        }
    }

    @Override
    public int xSize() {
        return xSize;
    }

    @Override
    public int ySize() {
        return ySize;
    }

    /**
     * Must call at the end of init after page count is init (or call {@link BaseScreen#updateButtonVisibility()} afterwards
     *
     * @param back   Whether to add a back button
     * @param search Whether to add a search button
     */
    protected void addButtons(boolean back, boolean search) {
        addRenderableWidget(btnNext = NavigationButton.create(book.getPageTexture(), guiLeft() + 4 * xSize() / 6, guiTop() + 5 * ySize() / 6, NavigationButton.TYPE.NEXT, btn -> pageNext()));
        addRenderableWidget(btnPrev = NavigationButton.create(book.getPageTexture(), guiLeft() + xSize() / 5, guiTop() + 5 * ySize() / 6, NavigationButton.TYPE.PREV, btn -> pagePrev()));
        if (back) {
            addRenderableWidget(btnBack = NavigationButton.create(book.getPageTexture(), guiLeft() + xSize() / 6, guiTop(), NavigationButton.TYPE.BACK, btn -> goBack()));
        }
        if (search) {
            addRenderableWidget(NavigationButton.create(book.getPageTexture(), (guiLeft() + xSize() / 6) - 25, guiTop() + 5, NavigationButton.TYPE.SEARCH, btn -> startSearch()));
        }
        updateButtonVisibility();
    }

    protected void goBack() {

    }

    @MustBeInvokedByOverriders
    @Override
    protected void init() {
        guiLeft = (this.width - this.xSize()) / 2;
        guiTop = (this.height - this.ySize()) / 2;
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

    protected void startSearch() {

    }

    private void updateButtonVisibility() {
        btnNext.visible = currentPage < getPageCount() - 1;
        btnPrev.visible = currentPage > 0;

    }
}
