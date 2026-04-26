package de.maxanier.guideapi.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.book.IGuideBook;
import de.maxanier.guideapi.api.world.BlockIdentifier;
import de.maxanier.guideapi.api.world.IInfoRenderer;
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

public class GuideAPI {

    private static final Map<Identifier, Book> BOOKS = Maps.newHashMap();
    private static final Map<Book, Holder<Item>> BOOK_TO_ITEM = Maps.newHashMap();
    private static final Map<Book, List<Pair<BlockIdentifier, IInfoRenderer>>> INFO_RENDERERS = Maps.newHashMap();
    private static final List<Book> indexedBooks = Lists.newArrayList();

    /**
     * @return A holder of the book's item
     */
    public static Holder<Item> getItemForBook(Book book) {
        return BOOK_TO_ITEM.get(book);
    }

    /**
     * Registers an IInfoRenderer. Do this from {@link IGuideBook#registerInfoRenderer(Book)}
     *
     * @param infoRenderer - The renderer to register
     * @param blocks       - The blocks that this should draw for
     */
    public static void registerInfoRenderer(Book book, IInfoRenderer infoRenderer, Block... blocks) {
        registerInfoRenderer(book, infoRenderer, new BlockIdentifier(blocks));
    }

    public static void registerInfoRenderer(Book book, IInfoRenderer infoRenderer, TagKey<Block> blocks) {
        registerInfoRenderer(book, infoRenderer, new BlockIdentifier(blocks));
    }

    public static void registerInfoRenderer(Book book, IInfoRenderer infoRenderer, Holder<Block> block) {
        registerInfoRenderer(book, infoRenderer, new BlockIdentifier(block));
    }

    private static void registerInfoRenderer(Book book, IInfoRenderer infoRenderer, BlockIdentifier... blocks) {
        if (!INFO_RENDERERS.containsKey(book))
            INFO_RENDERERS.put(book, new ArrayList<>());

        for (BlockIdentifier block : blocks)
            INFO_RENDERERS.get(book).add(Pair.of(block, infoRenderer));
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

    @Nullable
    public static IInfoRenderer getInfoRendererForBlock(Book book, Block block) {
        List<Pair<BlockIdentifier, IInfoRenderer>> bookRenderers = INFO_RENDERERS.get(book);
        if (bookRenderers == null)
            return null;
        List<IInfoRenderer> renderers = new ArrayList<>();
        for (Pair<BlockIdentifier, IInfoRenderer> bookRenderer : bookRenderers) {
            if (bookRenderer.getLeft().matches(block)) {
                return bookRenderer.getRight();
            }
        }
        return null;
    }

}
