package de.maxanier.guideapi.core.gui.wrapper;

import de.maxanier.guideapi.api.GuideBookScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;

public abstract class AbstractWrapper {

    public abstract boolean canPlayerSee();

    public abstract void draw(GuiGraphics graphics, int mouseX, int mouseY, GuideBookScreen gui);

    public abstract void drawExtras(GuiGraphics graphics, int mouseX, int mouseY, GuideBookScreen gui);

    public abstract boolean isMouseOnWrapper(double mouseX, double mouseY);

    public abstract void onHoverOver(int mouseX, int mouseY);
}
