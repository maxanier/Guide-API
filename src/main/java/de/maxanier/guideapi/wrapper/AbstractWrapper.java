package de.maxanier.guideapi.wrapper;

import de.maxanier.guideapi.gui.BaseScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;

public abstract class AbstractWrapper {

    public abstract boolean canPlayerSee();

    public abstract void draw(GuiGraphics graphics, RegistryAccess registryAccess, int mouseX, int mouseY, BaseScreen gui);

    public abstract void drawExtras(GuiGraphics graphics, int mouseX, int mouseY, BaseScreen gui);

    public abstract boolean isMouseOnWrapper(double mouseX, double mouseY);

    public abstract void onHoverOver(int mouseX, int mouseY);
}
