package de.maxanier.guideapi.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.book.IGuideBook;
import de.maxanier.guideapi.api.world.BlockIdentifier;
import de.maxanier.guideapi.api.world.IInfoOverlay;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class GuideAPI {

    private static final Map<Identifier, Book> BOOKS = Maps.newHashMap();
    private static final Map<Book, Holder<Item>> BOOK_TO_ITEM = Maps.newHashMap();
    private static final Map<Book, List<Pair<BlockIdentifier, Supplier<IInfoOverlay>>>> INFO_OVERLAYS = Maps.newHashMap();
    private static final List<Book> indexedBooks = Lists.newArrayList();

    /**
     * @return A holder of the book's item
     */
    public static Holder<Item> getItemForBook(Book book) {
        return BOOK_TO_ITEM.get(book);
    }

    /**
     * Registers an IInfoRenderer. Do this from {@link IGuideBook#registerInfoOverlays(Book)}
     *
     * @param infoRenderer - The renderer to register
     * @param blocks       - The blocks that this should draw for
     */
    public static void registerInfoOverlay(Book book, Supplier<IInfoOverlay> infoRenderer, Block... blocks) {
        registerInfoOverlay(book, infoRenderer, new BlockIdentifier(blocks));
    }

    public static void registerInfoOverlay(Book book, Supplier<IInfoOverlay> infoRenderer, TagKey<Block> blocks) {
        registerInfoOverlay(book, infoRenderer, new BlockIdentifier(blocks));
    }

    public static void registerInfoOverlay(Book book, Supplier<IInfoOverlay> infoRenderer, Holder<Block> block) {
        registerInfoOverlay(book, infoRenderer, new BlockIdentifier(block));
    }

    private static void registerInfoOverlay(Book book, Supplier<IInfoOverlay> infoRenderer, BlockIdentifier... blocks) {
        if (!INFO_OVERLAYS.containsKey(book))
            INFO_OVERLAYS.put(book, new ArrayList<>());

        for (BlockIdentifier block : blocks)
            INFO_OVERLAYS.get(book).add(Pair.of(block, infoRenderer));
    }

    public static void initialize() {
        // No-op. Just here to initialize fields.
    }

    public static Map<Identifier, Book> getBooks() {
        return ImmutableMap.copyOf(BOOKS);
    }

    public static Map<Book, Holder<Item>> getBookToItem() {
        return ImmutableMap.copyOf(BOOK_TO_ITEM);
    }

    public static List<Book> getIndexedBooks() {
        return ImmutableList.copyOf(indexedBooks);
    }

    /**
     * Please cache the created InfoRenderer
     *
     * @return An InfoRenderer provided by the book for the given block or null
     */
    @Nullable
    public static IInfoOverlay getInfoOverlay(Book book, Block block) {
        List<Pair<BlockIdentifier, Supplier<IInfoOverlay>>> bookRenderers = INFO_OVERLAYS.get(book);
        if (bookRenderers == null)
            return null;
        for (Pair<BlockIdentifier, Supplier<IInfoOverlay>> bookRenderer : bookRenderers) {
            if (bookRenderer.getLeft().matches(block)) {
                return bookRenderer.getRight().get();
            }
        }
        return null;
    }

}
