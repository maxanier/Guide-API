package de.maxanier.guideapi.core.gui;

import com.mojang.blaze3d.platform.InputConstants;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.pages.IPage;
import de.maxanier.guideapi.core.gui.wrapper.PageWrapper;
import de.maxanier.guideapi.core.network.ReadingStatePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntryScreen extends BaseScreen {

    public CategoryBase category;
    public EntryBase entry;
    public List<PageWrapper> pageWrapperList = new ArrayList<PageWrapper>();

    public EntryScreen(Book book, CategoryBase category, EntryBase entry, Player player) {
        super(entry.name, book, player);
        this.category = category;
        this.entry = entry;
    }


    @Override
    public int getPageCount() {
        return pageWrapperList.size();
    }

    @Override
    public void init() {
        super.init();
        entry.onInit(getMinecraft().level.registryAccess(), book, category, player());
        this.pageWrapperList.clear();


        if (this.entry.pageList.isEmpty()) {
            throw new IllegalStateException("Empty book entry " + entry.name.toString());
        }
        for (IPage page : this.entry.pageList) {
            page.onInit(getMinecraft().level.registryAccess(), book, category, entry, player());
            pageWrapperList.add(new PageWrapper(this, book, category, entry, page, pageLeft(), pageTop(), player(), this.font));
        }


        addButtons(true, true);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            for (PageWrapper wrapper : this.pageWrapperList) {
                if (wrapper.isMouseOnWrapper(event.x(), event.y()) && wrapper.canPlayerSee()) {
                    if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
                        pageWrapperList.get(currentPage()).page.onLeftClicked(book, category, entry, event.x(), event.y(), player(), this);
                        return true;
                    }
                    if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                        pageWrapperList.get(currentPage()).page.onRightClicked(book, category, entry, event.x(), event.y(), player(), this);
                        return true;
                    }
                }
            }

            if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                this.minecraft.setScreen(new CategoryScreen(book, category, player(), entry));
                return true;
            }
            return false;
        }
        return true;

    }

    @Override
    public void onClose() {
        super.onClose();
        for (IPage page : this.entry.pageList) {
            page.onClose();
        }
        Identifier key = null;
        for (Map.Entry<Identifier, EntryBase> mapEntry : category.entries.entrySet())
            if (mapEntry.getValue().equals(entry))
                key = mapEntry.getKey();

        if (key != null) {
            ClientPacketDistributor.sendToServer(new ReadingStatePayload(currentPage(), Optional.of(book.getCategoryList().indexOf(category)), Optional.of(key)));
        }
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float renderPartialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, renderPartialTicks);

        if (currentPage() < pageWrapperList.size()) {
            if (pageWrapperList.get(currentPage()).canPlayerSee()) {
                pageWrapperList.get(currentPage()).draw(graphics, mouseX, mouseY, this);
                pageWrapperList.get(currentPage()).drawExtras(graphics, mouseX, mouseY, this);
            }
        }

        graphics.centeredText(font, entry.getName(), pageXCenter(), screenTop() - 10, Color.WHITE.getRGB());

    }

    @Override
    protected void goBack() {
        this.minecraft.setScreen(new CategoryScreen(book, category, player(), entry));

    }

    @Override
    protected void startSearch() {
        this.minecraft.setScreen(new SearchScreen(book, player(), this));

    }
}
