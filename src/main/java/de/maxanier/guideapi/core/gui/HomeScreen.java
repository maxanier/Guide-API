package de.maxanier.guideapi.core.gui;

import com.google.common.collect.HashMultimap;
import com.mojang.blaze3d.platform.InputConstants;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.core.gui.wrapper.CategoryWrapper;
import de.maxanier.guideapi.core.network.ReadingStatePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;

public class HomeScreen extends BaseScreen {

    public HashMultimap<Integer, CategoryWrapper> categoryWrapperMap = HashMultimap.create();
    public int categoryPage;

    public HomeScreen(Book book, Player player) {
        super(book.getTitle(), book, player);
    }

    @Override
    public int getPageCount() {
        return categoryWrapperMap.asMap().size();
    }

    @Override
    public void init() {
        super.init();
        this.categoryWrapperMap.clear();


        int cX = guiLeft() + 55;
        int cY = guiTop() + 40;
        int i = 0;
        int pageNumber = 0;

        for (CategoryBase category : book.getCategoryList()) {
            if (category.entries.isEmpty())
                continue;

            category.onInit(book, player());
            int x = i % 5;
            int y = i / 5;
            categoryWrapperMap.put(pageNumber, new CategoryWrapper(book, category, cX + x * 27, cY + y * 30, 23, 23, player(), this.font, false));
            i++;

            if (i >= 20) {
                i = 0;
                pageNumber++;
            }
        }

        super.addButtons(false, true);

    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            for (CategoryWrapper wrapper : this.categoryWrapperMap.get(currentPage())) {
                if (wrapper.isMouseOnWrapper(event.x(), event.y()) && wrapper.canPlayerSee()) {
                    if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
                        wrapper.category.onLeftClicked(book, event.x(), event.y(), player());
                        return true;
                    } else if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                        wrapper.category.onRightClicked(book, event.x(), event.y(), player());
                        return true;
                    }
                }
            }
            return false;
        }
        return true;

    }

    @Override
    public void onClose() {
        super.onClose();
        ClientPacketDistributor.sendToServer(new ReadingStatePayload(currentPage(), Optional.empty(), Optional.empty()));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float renderPartialTicks) {
        super.render(graphics, mouseX, mouseY, renderPartialTicks);
//        ActiveTextCollector textCollector = graphics.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR);
//        textCollector.accept(guiLeft + xSize / 2 + 1, guiTop + 15, book.getHeader());

        GuiHelper.drawCenteredStringWithoutShadow(graphics, font, book.getHeader(), guiLeft() + xSize() / 2 + 1, guiTop() + 15, book.getTextColor());

        for (CategoryWrapper wrapper : this.categoryWrapperMap.get(currentPage()))
            if (wrapper.canPlayerSee())
                wrapper.draw(graphics, mouseX, mouseY, this);

        for (CategoryWrapper wrapper : this.categoryWrapperMap.get(currentPage()))
            if (wrapper.canPlayerSee())
                wrapper.drawExtras(graphics, mouseX, mouseY, this);

        graphics.drawCenteredString(font, book.getTitle(), guiLeft() + xSize() / 2, guiTop() - 10, Color.WHITE.getRGB());

    }

    @Override
    protected void startSearch() {
        minecraft.setScreen(new SearchScreen(book, player(), this));
    }
}
