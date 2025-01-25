package de.maxanier.guideapi.page;

import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.Page;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.gui.BaseScreen;
import de.maxanier.guideapi.gui.EntryScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * Render an entity in the middle of the page.
 * The entity is created on GUI init with the local MC world and removed (garbage collector) when closing the screen.
 */
public class PageEntity extends Page {

    @Nullable
    private final Component title;
    private final BiFunction<Level, EntitySpawnReason, ? extends LivingEntity> supplier;
    @Nullable
    private LivingEntity e;

    public PageEntity(EntityType<? extends LivingEntity> entityType) {
        this(entityType::create, entityType.getDescription());
    }

    /**
     * @param supplier Supply a (new) entity instance
     * @param title    Title to render below the entity. If null, the name of the entity will be rendered
     */
    public PageEntity(BiFunction<Level, EntitySpawnReason, ? extends LivingEntity> supplier, @Nullable Component title) {
        this.supplier = supplier;
        this.title = title;
    }

    public PageEntity(BiFunction<Level, EntitySpawnReason, ? extends LivingEntity> supplier) {
        this(supplier, null);
    }

    @Override
    public void draw(GuiGraphics graphics, RegistryAccess registryAccess, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj) {
        if (e != null)
            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, guiLeft + guiBase.xSize/2 - 60, guiTop + guiBase.ySize/2 - 80 , guiLeft + guiBase.xSize/2 +60 , guiTop + guiBase.ySize/2 + 40, 50,0.0625f, mouseX, mouseY, this.e);

    }

    @Override
    public void drawExtras(GuiGraphics graphics, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, Font fontRendererObj) {

        if (e != null)
            guiBase.drawCenteredStringWithoutShadow(graphics, fontRendererObj, (title != null ? title : e.getName()), guiLeft + guiBase.xSize / 2, guiTop + 140, 0x050505);
    }

    @Override
    public void onClose() {
        this.e = null;
    }

    @Override
    public void onInit(Book book, CategoryAbstract category, EntryAbstract entry, Player player, ItemStack bookStack, EntryScreen guiEntry) {
        if (guiEntry.getMinecraft().level != null)
            this.e = supplier.apply(guiEntry.getMinecraft().level, EntitySpawnReason.MOB_SUMMONED);
    }
}