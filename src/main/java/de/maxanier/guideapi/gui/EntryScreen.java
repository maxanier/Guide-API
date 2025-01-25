package de.maxanier.guideapi.gui;

import com.mojang.blaze3d.platform.InputConstants;
import de.maxanier.guideapi.api.IPage;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.network.ReadingStatePayload;
import de.maxanier.guideapi.wrapper.PageWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntryScreen extends BaseScreenWithNavigation {

    public CategoryAbstract category;
    public EntryAbstract entry;
    public List<PageWrapper> pageWrapperList = new ArrayList<PageWrapper>();

    public EntryScreen(Book book, CategoryAbstract category, EntryAbstract entry, Player player, ItemStack bookStack) {
        super(entry.name, book, player, bookStack);
        this.category = category;
        this.entry = entry;
    }


    @Override
    protected int getPageCount() {
        return pageWrapperList.size();
    }

    @Override
    protected void goBack() {
        this.minecraft.setScreen(new CategoryScreen(book, category, player, bookStack, entry));

    }

    @Override
    protected void startSearch() {
        this.minecraft.setScreen(new SearchScreen(book, player, bookStack, this));

    }

    @Override
    public void init() {
        super.init();
        entry.onInit(book, category, null, player, bookStack);
        this.pageWrapperList.clear();



        if(this.entry.pageList.isEmpty()){
            throw new IllegalStateException("Empty book entry "+entry.name.toString());
        }
        for (IPage page : this.entry.pageList) {
            page.onInit(book, category, entry, player, bookStack, this);
            pageWrapperList.add(new PageWrapper(this, book, category, entry, page, guiLeft, guiTop, player, this.font, bookStack));
        }


        addButtons(true, true);
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            for (PageWrapper wrapper : this.pageWrapperList) {
                if (wrapper.isMouseOnWrapper(event.x(), event.y()) && wrapper.canPlayerSee()) {
                    if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
                        pageWrapperList.get(currentPage()).page.onLeftClicked(book, category, entry, event.x(), event.y(), player, this);
                        return true;
                    }
                    if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                        pageWrapperList.get(currentPage()).page.onRightClicked(book, category, entry, event.x(), event.y(), player, this);
                        return true;
                    }
                }
            }

            if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                this.minecraft.setScreen(new CategoryScreen(book, category, player, bookStack, entry));
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
        for (Map.Entry<Identifier, EntryAbstract> mapEntry : category.entries.entrySet())
            if (mapEntry.getValue().equals(entry))
                key = mapEntry.getKey();

        if (key != null) {
            ClientPacketDistributor.sendToServer(new ReadingStatePayload(currentPage(), Optional.of(book.getCategoryList().indexOf(category)), Optional.of(key)));
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float renderPartialTicks) {
        super.render(graphics, mouseX, mouseY, renderPartialTicks);

        if (currentPage() < pageWrapperList.size()) {
            if (pageWrapperList.get(currentPage()).canPlayerSee()) {
                pageWrapperList.get(currentPage()).draw(graphics, Minecraft.getInstance().level.registryAccess(), mouseX, mouseY, this);
                pageWrapperList.get(currentPage()).drawExtras(graphics, mouseX, mouseY, this);
            }
        }

        graphics.drawCenteredString(font, entry.getName(), guiLeft + xSize / 2, guiTop - 10, Color.WHITE.getRGB());

    }
}
