package de.maxanier.guideapi.core.gui;

import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import net.minecraft.world.entity.player.Player;

/**
 * Simple GuiEntry which back button leads to a previous entry and not to the category page
 */
public class LinkedEntryScreen extends EntryScreen {

    private final EntryBase from;
    private final int fromPage;

    public LinkedEntryScreen(Book book, CategoryBase category, EntryBase entry, Player player, EntryBase from, int fromPage) {
        super(book, category, entry, player);
        this.from = from;
        this.fromPage = fromPage;
    }

    @Override
    protected void goBack() {
        EntryScreen e = new EntryScreen(book, category, from, player());
        this.minecraft.setScreen(e);
        e.setPage(fromPage);
    }
}
