package de.maxanier.guideapi.api;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;


public interface GuideBookScreen {


    int currentPage();

    Minecraft getMinecraft();

    int getPageCount();

    int guiLeft();

    int guiTop();

    Player player();

    int xSize();

    int ySize();
}
