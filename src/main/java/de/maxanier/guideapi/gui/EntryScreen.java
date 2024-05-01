package de.maxanier.guideapi.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import de.maxanier.guideapi.api.IPage;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.button.ButtonBack;
import de.maxanier.guideapi.button.ButtonNext;
import de.maxanier.guideapi.button.ButtonPrev;
import de.maxanier.guideapi.button.ButtonSearch;
import de.maxanier.guideapi.network.ReadingStatePayload;
import de.maxanier.guideapi.wrapper.PageWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntryScreen extends BaseScreen {

    public ResourceLocation outlineTexture;
    public ResourceLocation pageTexture;
    public Book book;
    public CategoryAbstract category;
    public EntryAbstract entry;
    public List<PageWrapper> pageWrapperList = new ArrayList<PageWrapper>();
    public ButtonBack buttonBack;
    public ButtonNext buttonNext;
    public ButtonPrev buttonPrev;
    public ButtonSearch buttonSearch;
    public int pageNumber;

    public EntryScreen(Book book, CategoryAbstract category, EntryAbstract entry, Player player, ItemStack bookStack) {
        super(entry.name, player, bookStack);
        this.book = book;
        this.category = category;
        this.entry = entry;
        this.pageTexture = book.getPageTexture();
        this.outlineTexture = book.getOutlineTexture();
        this.pageNumber = 0;
    }


    @Override
    public void init() {
        super.init();
        entry.onInit(book, category, null, player, bookStack);
        this.pageWrapperList.clear();

        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;

        addRenderableWidget(buttonBack = new ButtonBack(guiLeft + xSize / 6, guiTop, (btn) -> {
            this.minecraft.setScreen(new CategoryScreen(book, category, player, bookStack, entry));

        }, this));
        addRenderableWidget(buttonNext = new ButtonNext(guiLeft + 4 * xSize / 6, guiTop + 5 * ySize / 6, (btn) -> {
            if (pageNumber + 1 < pageWrapperList.size()) {
                nextPage();
            }
        }, this));
        addRenderableWidget(buttonPrev = new ButtonPrev(guiLeft + xSize / 5, guiTop + 5 * ySize / 6, (btn) -> {
            if (pageNumber > 0) {
                prevPage();
            }
        }, this));
        addRenderableWidget(buttonSearch = new ButtonSearch((guiLeft + xSize / 6) - 25, guiTop + 5, (btn) -> {
            this.minecraft.setScreen(new SearchScreen(book, player, bookStack, this));
        }, this));

        for (IPage page : this.entry.pageList) {
            page.onInit(book, category, entry, player, bookStack, this);
            pageWrapperList.add(new PageWrapper(this, book, category, entry, page, guiLeft, guiTop, player, this.font, bookStack));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == this.minecraft.options.keyUse.getKey().getValue()) {
            this.minecraft.setScreen(new CategoryScreen(book, category, player, bookStack, entry));
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_RIGHT) && pageNumber + 1 < pageWrapperList.size()) {
            nextPage();
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_LEFT) && pageNumber > 0) {
            prevPage();
            return true;
        }
        return super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int typeofClick) {
        if (!super.mouseClicked(mouseX, mouseY, typeofClick)) {
            for (PageWrapper wrapper : this.pageWrapperList) {
                if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                    if (typeofClick == 0) {
                        pageWrapperList.get(pageNumber).page.onLeftClicked(book, category, entry, mouseX, mouseY, player, this);
                        return true;
                    }
                    if (typeofClick == 1) {
                        pageWrapperList.get(pageNumber).page.onRightClicked(book, category, entry, mouseX, mouseY, player, this);
                        return true;
                    }
                }
            }

            if (typeofClick == 1) {
                this.minecraft.setScreen(new CategoryScreen(book, category, player, bookStack, entry));
                return true;
            }
            return false;
        }
        return true;

    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {

        if (pScrollY < 0)
            nextPage();
        else if (pScrollY > 0)
            prevPage();


        return pScrollY != 0 || super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);

    }

    public void nextPage() {
        if (pageNumber != pageWrapperList.size() - 1 && !pageWrapperList.isEmpty())
            pageNumber++;
    }

    @Override
    public void onClose() {
        super.onClose();
        for (IPage page : this.entry.pageList) {
            page.onClose();
        }
        ResourceLocation key = null;
        for (Map.Entry<ResourceLocation, EntryAbstract> mapEntry : category.entries.entrySet())
            if (mapEntry.getValue().equals(entry))
                key = mapEntry.getKey();

        if (key != null)
            PacketDistributor.sendToServer(new ReadingStatePayload(pageNumber, Optional.of(book.getCategoryList().indexOf(category)), Optional.of(key)));
    }

    public void prevPage() {
        if (pageNumber != 0)
            pageNumber--;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(graphics, pMouseX, pMouseY, pPartialTick);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(pageTexture, guiLeft, guiTop, 0, 0, xSize, ySize);
        graphics.setColor((float) book.getColor().getRed() / 255F, (float) book.getColor().getGreen() / 255F, (float) book.getColor().getBlue() / 255F, 1f);
        graphics.blit(outlineTexture, guiLeft, guiTop, 0, 0, xSize, ySize);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float renderPartialTicks) {
        super.render(graphics, mouseX, mouseY, renderPartialTicks);


        pageNumber = Mth.clamp(pageNumber, 0, pageWrapperList.size() - 1);

        if (pageNumber < pageWrapperList.size()) {
            if (pageWrapperList.get(pageNumber).canPlayerSee()) {
                pageWrapperList.get(pageNumber).draw(graphics, Minecraft.getInstance().level.registryAccess(), mouseX, mouseY, this);
                pageWrapperList.get(pageNumber).drawExtras(graphics, mouseX, mouseY, this);
            }
        }

        drawCenteredStringWithoutShadow(graphics, font, String.format("%d/%d", pageNumber + 1, pageWrapperList.size()), guiLeft + xSize / 2, guiTop + 5 * ySize / 6, 0);
        graphics.drawCenteredString(font, entry.getName(), guiLeft + xSize / 2, guiTop - 10, Color.WHITE.getRGB());

        buttonPrev.visible = pageNumber != 0;
        buttonNext.visible = pageNumber != pageWrapperList.size() - 1 && !pageWrapperList.isEmpty();

    }
}
