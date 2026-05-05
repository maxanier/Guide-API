package de.maxanier.guideapi.core.gui;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.core.gui.wrapper.EntryWrapper;
import de.maxanier.guideapi.core.network.ReadingStatePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class CategoryScreen extends BaseScreen {

    public CategoryBase category;
    public HashMultimap<Integer, EntryWrapper> entryWrapperMap = HashMultimap.create();
    @Nullable
    public EntryBase startEntry;

    public CategoryScreen(Book book, CategoryBase category, Player player, @Nullable EntryBase startEntry) {
        super(category.name, book, player);
        this.category = category;
        this.startEntry = startEntry;
    }

    @Override
    public int getPageCount() {
        return entryWrapperMap.asMap().size();
    }

    @Override
    public void init() {
        super.init();
        this.entryWrapperMap.clear();

        int topOffset = 5;
        int eX = pageLeft();
        int eY = pageTop() + topOffset;
        int i = 0;
        int pageNumber = 0;
        int startPageNumber = 0; //We can only set the page after the buttons have been initiated, so store start page until the end
        List<EntryBase> entries = Lists.newArrayList(category.entries.values());
        for (EntryBase entry : entries) {
            entry.onInit(getMinecraft().level.registryAccess(), book, category, player());
            entryWrapperMap.put(pageNumber, new EntryWrapper(this, book, category, entry, eX, eY, pageWidth(), 10, player(), this.font));
            if (entry.equals(this.startEntry)) {
                this.startEntry = null;
                startPageNumber = pageNumber;
            }
            eY += 13;
            i++;

            if (i >= 11) {
                i = 0;
                eY = pageTop() + topOffset;
                pageNumber++;
            }
        }

        addButtons(true, true);
        this.setPage(startPageNumber);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean ret = super.mouseClicked(event, doubleClick);

        for (EntryWrapper wrapper : this.entryWrapperMap.get(currentPage())) {
            if (wrapper.isMouseOnWrapper(event.x(), event.y()) && wrapper.canPlayerSee()) {
                if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
                    wrapper.entry.onLeftClicked(book, category, event.x(), event.y(), player());
                    return true;
                } else if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                    wrapper.entry.onRightClicked(book, category, event.x(), event.y(), player());
                    return true;
                }
            }
        }

        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT)
            this.minecraft.setScreen(new HomeScreen(book, player()));
        return ret;
    }

    @Override
    public void onClose() {
        super.onClose();
        ClientPacketDistributor.sendToServer(new ReadingStatePayload(currentPage(), Optional.of(book.getCategoryList().indexOf(category)), Optional.empty()));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float renderPartialTicks) {
        super.render(graphics, mouseX, mouseY, renderPartialTicks);

        for (EntryWrapper wrapper : this.entryWrapperMap.get(currentPage())) {
            if (wrapper.canPlayerSee()) {
                wrapper.draw(graphics, mouseX, mouseY, this);
                wrapper.drawExtras(graphics, mouseX, mouseY, this);
            }
            if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                wrapper.onHoverOver(mouseX, mouseY);
            }
        }

        graphics.drawCenteredString(font, category.getName(), pageXCenter(), screenTop() - 10, Color.WHITE.getRGB());

    }

    @Override
    protected void goBack() {
        this.minecraft.setScreen(new HomeScreen(book, player()));
    }

    @Override
    protected void startSearch() {
        this.minecraft.setScreen(new SearchScreen(book, player(), this));
    }
}
