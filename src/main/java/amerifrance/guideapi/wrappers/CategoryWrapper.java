package amerifrance.guideapi.wrappers;

import amerifrance.guideapi.gui.GuiCategory;
import amerifrance.guideapi.objects.Book;
import amerifrance.guideapi.objects.Category;
import amerifrance.guideapi.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class CategoryWrapper extends AbstractWrapper {

    public Book book;
    public Category category;
    public int x, y, width, height;
    public EntityPlayer player;

    public CategoryWrapper(Book book, Category category, int x, int y, int width, int height, EntityPlayer player) {
        this.book = book;
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.player = player;
    }

    @Override
    public void onClicked() {
        System.out.println(category.getLocalizedName());
        Minecraft.getMinecraft().displayGuiScreen(new GuiCategory(book, category, player));
    }

    @Override
    public void onHoverOver(int mouseX, int mouseY) {
        Minecraft.getMinecraft().fontRenderer.drawString(category.getLocalizedName(), mouseX + 6, mouseY, 0);
    }

    @Override
    public boolean canPlayerSee(EntityPlayer player) {
        return true;
    }

    public boolean canPlayerSee() {
        return canPlayerSee(player);
    }

    @Override
    public void draw() {
        GuiHelper.drawItemStack(category.stack(), x, y);
    }

    @Override
    public boolean isMouseOnWrapper(int mouseX, int mouseY) {
        return GuiHelper.isMouseBetween(mouseX, mouseY, x, y, width, height);
    }
}
