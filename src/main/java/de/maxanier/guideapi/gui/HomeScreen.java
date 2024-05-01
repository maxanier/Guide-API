package de.maxanier.guideapi.gui;

import com.google.common.collect.HashMultimap;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.button.ButtonNext;
import de.maxanier.guideapi.button.ButtonPrev;
import de.maxanier.guideapi.button.ButtonSearch;
import de.maxanier.guideapi.network.ReadingStatePayload;
import de.maxanier.guideapi.wrapper.CategoryWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Optional;

public class HomeScreen extends BaseScreen {

    public ResourceLocation outlineTexture;
    public ResourceLocation pageTexture;
    public Book book;
    public HashMultimap<Integer, CategoryWrapper> categoryWrapperMap = HashMultimap.create();
    public ButtonNext buttonNext;
    public ButtonPrev buttonPrev;
    public ButtonSearch buttonSearch;
    public int categoryPage;

    public HomeScreen(Book book, Player player, ItemStack bookStack) {
        super(book.getTitle(), player, bookStack);
        this.book = book;
        this.pageTexture = book.getPageTexture();
        this.outlineTexture = book.getOutlineTexture();
        this.categoryPage = 0;
    }

    @Override
    public void init() {
        this.categoryWrapperMap.clear();

        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;

        addRenderableWidget(buttonNext = new ButtonNext(guiLeft + 4 * xSize / 6, guiTop + 5 * ySize / 6, (btn) -> {
            if (categoryPage + 1 < categoryWrapperMap.asMap().size()) {
                nextPage();
            }
        }, this));
        addRenderableWidget(buttonPrev = new ButtonPrev(guiLeft + xSize / 5, guiTop + 5 * ySize / 6, (btn) -> {
            if (categoryPage > 0) {
                prevPage();
            }
        }, this));
        addRenderableWidget(buttonSearch = new ButtonSearch((guiLeft + xSize / 6) - 25, guiTop + 5, (btn) -> {
            minecraft.setScreen(new SearchScreen(book, player, bookStack, this));
        }, this));

        int cX = guiLeft + 55;
        int cY = guiTop + 40;
        int i = 0;
        int pageNumber = 0;

        for (CategoryAbstract category : book.getCategoryList()) {
            if (category.entries.isEmpty())
                continue;

            category.onInit(book, this, player, bookStack);
            int x = i % 5;
            int y = i / 5;
            categoryWrapperMap.put(pageNumber, new CategoryWrapper(book, category, cX + x * 27, cY + y * 30, 23, 23, player, this.font, false, bookStack));
            i++;

            if (i >= 20) {
                i = 0;
                pageNumber++;
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_) {
        if ((keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_RIGHT) && categoryPage + 1 < categoryWrapperMap.asMap().size()) {
            nextPage();
        } else if ((keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_LEFT) && categoryPage > 0) {
            prevPage();
        }

        return super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int typeofClick) {
        if (!super.mouseClicked(mouseX, mouseY, typeofClick)) {
            for (CategoryWrapper wrapper : this.categoryWrapperMap.get(categoryPage)) {
                if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                    if (typeofClick == 0)
                        wrapper.category.onLeftClicked(book, mouseX, mouseY, player, bookStack);


                    else if (typeofClick == 1)
                        wrapper.category.onRightClicked(book, mouseX, mouseY, player, bookStack);

                    return true;
                }
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
        if (categoryPage != categoryWrapperMap.asMap().size() - 1 && !categoryWrapperMap.asMap().isEmpty())
            categoryPage++;
    }

    @Override
    public void onClose() {
        super.onClose();
        PacketDistributor.sendToServer(new ReadingStatePayload(categoryPage, Optional.empty(), Optional.empty()));
    }

    public void prevPage() {
        if (categoryPage != 0)
            categoryPage--;
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

        drawCenteredStringWithoutShadow(graphics, font, book.getHeader().getVisualOrderText(), guiLeft + xSize / 2 + 1, guiTop + 15, 0);

        categoryPage = Mth.clamp(categoryPage, 0, categoryWrapperMap.size() - 1);

        for (CategoryWrapper wrapper : this.categoryWrapperMap.get(categoryPage))
            if (wrapper.canPlayerSee())
                wrapper.draw(graphics, Minecraft.getInstance().level.registryAccess(), mouseX, mouseY, this);

        for (CategoryWrapper wrapper : this.categoryWrapperMap.get(categoryPage))
            if (wrapper.canPlayerSee())
                wrapper.drawExtras(graphics, mouseX, mouseY, this);

        drawCenteredStringWithoutShadow(graphics, font, String.format("%d/%d", categoryPage + 1, categoryWrapperMap.asMap().size()), guiLeft + xSize / 2, guiTop + 5 * ySize / 6, 0);
        graphics.drawCenteredString(font, book.getTitle(), guiLeft + xSize / 2, guiTop - 10, Color.WHITE.getRGB());

        buttonPrev.visible = categoryPage != 0;
        buttonNext.visible = categoryPage != categoryWrapperMap.asMap().size() - 1 && !categoryWrapperMap.asMap().isEmpty();

    }
}
