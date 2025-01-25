package de.maxanier.guideapi.api.impl;

import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.gui.BaseScreen;
import de.maxanier.guideapi.gui.CategoryScreen;
import de.maxanier.guideapi.gui.HomeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;

public class Category extends CategoryAbstract {

    public Category(Map<Identifier, EntryAbstract> entryList, Component name) {
        super(entryList, name);
    }

    public Category(Component name) {
        super(name);
    }

    @Override
    public boolean canSee(Player player, ItemStack bookStack) {
        return true;
    }

    @Override
    public void draw(GuiGraphics graphics, Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX, int mouseY, BaseScreen guiBase, boolean drawOnLeft) {
    }

    @Override
    public void drawExtras(GuiGraphics graphics, Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX, int mouseY, BaseScreen guiBase, boolean drawOnLeft) {
    }

    @Override
    public void onInit(Book book, HomeScreen guiHome, Player player, ItemStack bookStack) {
    }

    @Override
    public void onLeftClicked(Book book, double mouseX, double mouseY, Player player, ItemStack bookStack) {
        Minecraft.getInstance().setScreen(new CategoryScreen(book, this, player, bookStack, null));
    }

    @Override
    public void onRightClicked(Book book, double mouseX, double mouseY, Player player, ItemStack bookStack) {
    }
}
