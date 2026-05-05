package de.maxanier.guideapi.api.pages;

import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.GuiHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    public void draw(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        if (e != null)
            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, screen.pageXCenter() - 60, screen.pageYCenter() - 80, screen.pageXCenter() + 60, screen.pageYCenter() + 40, 50, 0.0625f, mouseX, mouseY, this.e);

    }

    @Override
    public void drawExtras(GuiGraphics graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        if (e != null)
            GuiHelper.drawCenteredStringWithoutShadow(graphics, fontRendererObj, (title != null ? title : e.getName()), pageLeft + screen.pageWidth() / 2, pageTop - 13 + 140, 0xFF050505);
    }

    @Override
    public void onClose() {
        this.e = null;
    }

    @Override
    public void onInit(RegistryAccess registryAccess, Book book, CategoryBase category, EntryBase entry, Player player) {
        if (player.level() != null)
            this.e = supplier.apply(player.level(), EntitySpawnReason.MOB_SUMMONED);
    }
}