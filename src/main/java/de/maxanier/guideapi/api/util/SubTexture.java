package de.maxanier.guideapi.api.util;

import de.maxanier.guideapi.GuideMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public record SubTexture(Identifier textureLocation, int xPos, int yPos, int width, int height) {

    private static final Identifier RECIPE_ELEMENTS = Identifier.fromNamespaceAndPath(GuideMod.ID, "textures/gui/recipe_elements.png");
    // Grids
    public static final SubTexture CRAFTING_GRID = new SubTexture(RECIPE_ELEMENTS, 0, 48, 102, 56);
    public static final SubTexture FURNACE_GRID = new SubTexture(RECIPE_ELEMENTS, 0, 104, 68, 28);
    public static final SubTexture POTION_GRID = new SubTexture(RECIPE_ELEMENTS, 0, 132, 68, 81);
    // Singletons
    public static final SubTexture SINGLE_SLOT = new SubTexture(RECIPE_ELEMENTS, 0, 28, 20, 20);
    // Large buttons
    public static final SubTexture LARGE_BUTTON = new SubTexture(RECIPE_ELEMENTS, 0, 0, 55, 18);
    public static final SubTexture LARGE_BUTTON_HOVER = new SubTexture(RECIPE_ELEMENTS, 55, 0, 55, 18);
    public static final SubTexture LARGE_BUTTON_PRESS = new SubTexture(RECIPE_ELEMENTS, 110, 0, 55, 18);
    // Small buttons
    public static final SubTexture SMALL_BUTTON_BLANK = new SubTexture(RECIPE_ELEMENTS, 0, 0, 10, 10);
    public static final SubTexture SMALL_BUTTON_BLANK_HOVER = new SubTexture(RECIPE_ELEMENTS, 40, 0, 10, 10);
    public static final SubTexture SMALL_BUTTON_BLANK_PRESS = new SubTexture(RECIPE_ELEMENTS, 80, 0, 10, 10);
    public static final SubTexture SMALL_BUTTON_CRAFT = new SubTexture(RECIPE_ELEMENTS, 10, 0, 10, 10);
    public static final SubTexture SMALL_BUTTON_CRAFT_HOVER = new SubTexture(RECIPE_ELEMENTS, 50, 0, 10, 10);
    public static final SubTexture SMALL_BUTTON_CRAFT_PRESS = new SubTexture(RECIPE_ELEMENTS, 90, 0, 10, 10);
    public static final SubTexture SMALL_BUTTON_FURNACE = new SubTexture(RECIPE_ELEMENTS, 20, 0, 10, 10);
    public static final SubTexture SMALL_BUTTON_FURNACE_HOVER = new SubTexture(RECIPE_ELEMENTS, 60, 0, 10, 10);
    public static final SubTexture SMALL_BUTTON_FURNACE_PRESS = new SubTexture(RECIPE_ELEMENTS, 100, 0, 10, 10);
    public static final SubTexture SMALL_BUTTON_POTION = new SubTexture(RECIPE_ELEMENTS, 30, 0, 10, 10);
    public static final SubTexture SMALL_BUTTON_POTION_HOVER = new SubTexture(RECIPE_ELEMENTS, 70, 0, 10, 10);
    public static final SubTexture SMALL_BUTTON_POTION_PRESS = new SubTexture(RECIPE_ELEMENTS, 110, 0, 10, 10);
    private static final Identifier OTHER_ELEMENTS = Identifier.fromNamespaceAndPath(GuideMod.ID, "textures/gui/book_colored.png");

    public void draw(GuiGraphicsExtractor graphics, int drawX, int drawY, float zLevel) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, textureLocation, drawX, drawY, xPos, yPos, width, height, 256, 256);
    }

    public void draw(GuiGraphicsExtractor graphics, int drawX, int drawY) {
        draw(graphics, drawX, drawY, 0.1f);
    }
}
