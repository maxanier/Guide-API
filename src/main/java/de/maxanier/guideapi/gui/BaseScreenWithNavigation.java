package de.maxanier.guideapi.gui;

import com.mojang.blaze3d.platform.InputConstants;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.button.NavigationButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

import static de.maxanier.guideapi.button.NavigationButton.TYPE;

public abstract class BaseScreenWithNavigation extends BaseScreen {

    private NavigationButton btnNext;
    private NavigationButton btnPrev;
    @Nullable
    private NavigationButton btnBack;
    private int currentPage;

    public BaseScreenWithNavigation(Component title, Book book, Player player, ItemStack bookStack) {
        super(title, book, player, bookStack);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if ((keyCode == InputConstants.KEY_UP || keyCode == InputConstants.KEY_RIGHT)) {
            this.btnNext.onPress(event);
        } else if ((keyCode == InputConstants.KEY_DOWN || keyCode == InputConstants.KEY_LEFT)) {
            this.btnPrev.onPress(event);
        } else if (keyCode == InputConstants.KEY_BACKSPACE || keyCode == this.minecraft.options.keyUse.getKey().getValue()) {
            if (btnBack != null) {
                btnBack.onPress(event);
            }
        }

        return super.keyPressed(event);
    }

    public void setPage(int pageNum) {
        int i = Mth.clamp(pageNum, 0, getPageCount() - 1);
        if (i != this.currentPage) {
            this.currentPage = i;
            this.updateButtonVisibility();
        }
    }

    /**
     * Must call at the end of init after page count is init (or call {@link BaseScreenWithNavigation#updateButtonVisibility()} afterwards
     *
     * @param back   Whether to add a back button
     * @param search Whether to add a search button
     */
    protected void addButtons(boolean back, boolean search) {
        addRenderableWidget(btnNext = NavigationButton.create(book.getPageTexture(), guiLeft + 4 * xSize / 6, guiTop + 5 * ySize / 6, TYPE.NEXT, btn -> pageNext()));
        addRenderableWidget(btnPrev = NavigationButton.create(book.getPageTexture(), guiLeft + xSize / 5, guiTop + 5 * ySize / 6, NavigationButton.TYPE.PREV, btn -> pagePrev()));
        if (back) {
            addRenderableWidget(btnBack = NavigationButton.create(book.getPageTexture(), guiLeft + xSize / 6, guiTop, TYPE.BACK, btn -> goBack()));
        }
        if (search) {
            addRenderableWidget(NavigationButton.create(book.getPageTexture(), (guiLeft + xSize / 6) - 25, guiTop + 5, TYPE.SEARCH, btn -> startSearch()));
        }
        updateButtonVisibility();
    }

    public int currentPage() {
        return currentPage;
    }

    protected abstract int getPageCount();

    protected void goBack() {

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

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (getPageCount() > 1) {
            drawCenteredStringWithoutShadow(graphics, font, Component.literal(String.format("%d/%d", currentPage + 1, getPageCount())), guiLeft + xSize / 2, guiTop + 5 * ySize / 6);
        }
    }
}
