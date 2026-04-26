package de.maxanier.guideapi.core;

import com.google.common.base.Throwables;
import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.Category;
import de.maxanier.guideapi.api.entry.Entry;
import de.maxanier.guideapi.api.pages.PageHolderWithLinks;
import de.maxanier.guideapi.core.gui.CategoryScreen;
import de.maxanier.guideapi.core.gui.EntryScreen;
import de.maxanier.guideapi.core.gui.LinkedEntryScreen;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModLoadingContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * This class is for internal use <b>only</b>. Do not use this from outside.
 */
@SuppressWarnings("unchecked")
public class APISetter {

    public static void setScreenFactories() {
        try {
            sanityCheck();
        } catch (IllegalAccessException e) {
            Throwables.throwIfUnchecked(e);
            return;
        }

        try {
            Field createEntryScreen = Entry.class.getDeclaredField("createEntryScreen");
            createEntryScreen.setAccessible(true);
            createEntryScreen.set(null, (Entry.EntryScreenFactory) EntryScreen::new);

            Field createCategoryScreen = Category.class.getDeclaredField("createCategoryScreen");
            createCategoryScreen.setAccessible(true);
            createCategoryScreen.set(null, (Category.CategoryScreenFactory) CategoryScreen::new);

            Field createLinkedScreen = PageHolderWithLinks.class.getDeclaredField("createLinkedScreen");
            createLinkedScreen.setAccessible(true);
            createLinkedScreen.set(null, (PageHolderWithLinks.LinkedScreenFactory) LinkedEntryScreen::new);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerBook(Book book) {
        try {
            sanityCheck();
        } catch (IllegalAccessException e) {
            Throwables.throwIfUnchecked(e);
            return;
        }

        try {
            Field books = GuideAPI.class.getDeclaredField("BOOKS");
            books.setAccessible(true);
            Map<Identifier, Book> BOOKS = (Map<Identifier, Book>) books.get(null);
            BOOKS.put(book.getRegistryName(), book);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setBookForStack(Book book, Holder<Item> stack) {
        try {
            sanityCheck();
        } catch (IllegalAccessException e) {
            Throwables.propagate(e);
            return;
        }

        try {
            Field stacks = GuideAPI.class.getDeclaredField("BOOK_TO_ITEM");
            stacks.setAccessible(true);
            Map<Book, Holder<Item>> BOOK_TO_STACK = (Map<Book, Holder<Item>>) stacks.get(null);
            BOOK_TO_STACK.put(book, stack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setIndexedBooks(List<Book> books) {
        try {
            sanityCheck();
        } catch (IllegalAccessException e) {
            Throwables.propagate(e);
            return;
        }

        try {
            Field indexedBooks = GuideAPI.class.getDeclaredField("indexedBooks");
            indexedBooks.setAccessible(true);
            List<Book> list = (List<Book>) indexedBooks.get(null);
            list.clear();
            list.addAll(books);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sanityCheck() throws IllegalAccessException {
        String activeMod = ModLoadingContext.get().getActiveNamespace();
        if (!GuideMod.ID.equals(activeMod))
            throw new IllegalAccessException("Mod " + activeMod + " tried to access an internal-only method in GuideAPI. Please report this.");
    }
}
