package de.maxanier.guideapi.api;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;


public interface GuideBookScreen {


    /**
     * @return Width of the usable page area
     */
    int pageWidth();

    /**
     * @return Height of the usable page area
     */
    int pageHeight();

    /**
     * @return Horizontal (x) start of the usable page area
     */
    int pageLeft();

    /**
     * @return Vertical (y) start of the usable page area
     */
    int pageTop();

    /**
     * @return Horizontal center of the page
     */
    int pageXCenter();

    /**
     * @return Vertical center of the page
     */
    int pageYCenter();

    /**
     * @return The current page number
     */
    int currentPage();

    /**
     * @return The total number of pages
     */
    int getPageCount();

    Minecraft getMinecraft();

    Player player();

}
