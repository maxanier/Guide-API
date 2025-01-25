package de.maxanier.guideapi.gui;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.network.ReadingStatePayload;
import de.maxanier.guideapi.wrapper.EntryWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class CategoryScreen extends BaseScreenWithNavigation {

    public CategoryAbstract category;
    public HashMultimap<Integer, EntryWrapper> entryWrapperMap = HashMultimap.create();
    @Nullable
    public EntryAbstract startEntry;

    public CategoryScreen(Book book, CategoryAbstract category, Player player, ItemStack bookStack, @Nullable EntryAbstract startEntry) {
        super(category.name, book, player, bookStack);
        this.category = category;
        this.startEntry = startEntry;
    }

    @Override
    protected void goBack() {
        this.minecraft.setScreen(new HomeScreen(book, player, bookStack));
    }

    @Override
    protected void startSearch() {
        this.minecraft.setScreen(new SearchScreen(book, player, bookStack, this));
    }

    @Override
    protected int getPageCount() {
        return entryWrapperMap.asMap().size();
    }

    @Override
    public void init() {
        super.init();
        this.entryWrapperMap.clear();


        int eX = guiLeft + 37;
        int eY = guiTop + 15;
        int i = 0;
        int pageNumber = 0;
        List<EntryAbstract> entries = Lists.newArrayList(category.entries.values());
        for (EntryAbstract entry : entries) {
            entry.onInit(book, category, this, player, bookStack);
            entryWrapperMap.put(pageNumber, new EntryWrapper(this, book, category, entry, eX, eY, 4 * xSize / 6, 10, player, this.font, bookStack));
            if (entry.equals(this.startEntry)) {
                this.startEntry = null;
                this.setPage(pageNumber);
            }
            eY += 13;
            i++;

            if (i >= 11) {
                i = 0;
                eY = guiTop + 15;
                pageNumber++;
            }
        }

        addButtons(true, true);
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean ret = super.mouseClicked(event, doubleClick);

        for (EntryWrapper wrapper : this.entryWrapperMap.get(currentPage())) {
            if (wrapper.isMouseOnWrapper(event.x(), event.y()) && wrapper.canPlayerSee()) {
                if(event.button() == InputConstants.MOUSE_BUTTON_LEFT){
                    wrapper.entry.onLeftClicked(book, category, event.x(), event.y(), player, this);
                    return true;
                }
                else if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                    wrapper.entry.onRightClicked(book, category, event.x(), event.y(), player, this);
                    return true;
                }
            }
        }

        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT)
            this.minecraft.setScreen(new HomeScreen(book, player, bookStack));
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
                wrapper.draw(graphics, Minecraft.getInstance().level.registryAccess(), mouseX, mouseY, this);
                wrapper.drawExtras(graphics, mouseX, mouseY, this);
            }
            if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                wrapper.onHoverOver(mouseX, mouseY);
            }
        }

        graphics.drawCenteredString(font, category.getName(), guiLeft + xSize / 2, guiTop - 10, Color.WHITE.getRGB());


    }
}
