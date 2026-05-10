package de.maxanier.guideapi.api.book;

import com.google.common.base.Joiner;
import de.maxanier.guideapi.LogHelper;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.BiConsumer;

public class Book implements IBookContentCollector {

    private final List<CategoryBase> categories = new ArrayList<>();
    private final Map<Identifier, Identifier> linkedEntries = new HashMap<>();
    private final BiConsumer<RegistryAccess, IBookContentCollector> contentProvider;
    private final Component title;
    private final Component header;
    private final Component itemName;
    private final Component author;
    private final Identifier pageTexture;
    private final Identifier outlineTexture;
    private final int theme_color;
    private final int color_text;
    private final int color_text_highlight;
    private final boolean spawnWithBook;
    private final Identifier registryName;
    private boolean isInitialized;


    protected Book(BiConsumer<RegistryAccess, IBookContentCollector> contentProvider, Component title, Component header, Component displayName, Component author, Identifier pageTexture, Identifier outlineTexture, boolean spawnWithBook, Identifier registryName, int themeColor, int colorText, int colorTextHighlight) {
        this.contentProvider = contentProvider;
        this.title = title;
        this.header = header;
        this.itemName = displayName;
        this.author = author;
        this.pageTexture = pageTexture;
        this.outlineTexture = outlineTexture;
        this.theme_color = themeColor;
        this.color_text = colorText;
        this.color_text_highlight = colorTextHighlight;
        this.spawnWithBook = spawnWithBook;
        this.registryName = registryName;
    }

    @Override
    public void addBlockLinkedEntries(Map<Identifier, Identifier> linkedEntries) {
        this.linkedEntries.putAll(linkedEntries);
    }

    @Override
    public void addCategories(List<CategoryBase> categories) {
        this.categories.addAll(categories);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return getRegistryName().equals(book.getRegistryName());

    }

    /**
     * Can be used to force content initialization independent of first use regardless of if it was initialized previously.
     * Use at own risk. Might cause crashes if the book is currently open.
     */
    public void forceInitializeContent(RegistryAccess access) {
        LogHelper.info("Force initializing book content " + registryName.toString());
        categories.clear();
        isInitialized = false;
        initializeContent(access);
    }

    public Component getAuthor() {
        return this.author;
    }

    public List<CategoryBase> getCategoryList() {
        return this.categories;
    }

    public Component getHeader() {
        return this.header;
    }

    public Component getItemName() {
        return this.itemName;
    }

    public Identifier getOutlineTexture() {
        return this.outlineTexture;
    }

    public Identifier getPageTexture() {
        return this.pageTexture;
    }

    public Identifier getRegistryName() {
        return this.registryName;
    }

    public int getTextColor() {
        return color_text;
    }

    public int getTextColorHighlighted() {
        return color_text_highlight;
    }

    public int getThemeColor() {
        return this.theme_color;
    }

    public Component getTitle() {
        return this.title;
    }

    /**
     * Attempt to find the given entry id in any category
     *
     * @return Pair of category and entry
     */
    public Optional<Pair<CategoryBase, EntryBase>> findEntry(Identifier entryId) {
        for (CategoryBase category : getCategoryList()) {
            if (category.entries.containsKey(entryId)) {
                return Optional.of(Pair.of(category, category.getEntry(entryId)));
            }
        }
        return Optional.empty();
    }

    /**
     * @param blockIdentifier A block id
     * @return (Optional) Entry in this book about the given block
     */
    public Optional<Identifier> getLinkedEntryForBlock(Identifier blockIdentifier) {
        return Optional.ofNullable(linkedEntries.get(blockIdentifier));
    }

    @Override
    public int hashCode() {
        return getRegistryName().hashCode();
    }

    public void initializeContent(RegistryAccess registryAccess) {
        if (!isInitialized) {
            LogHelper.debug("Opening book " + registryName.toString() + " for the first time -> Initializing content");
            contentProvider.accept(registryAccess, this);
            for (CategoryBase category : categories) {
                for (Map.Entry<Identifier, EntryBase> resourceLocationEntryAbstractEntry : category.entries.entrySet()) {
                    if (resourceLocationEntryAbstractEntry.getValue().pageList.isEmpty()) {
                        throw new IllegalStateException("Empty entry " + resourceLocationEntryAbstractEntry.getKey().toString() + " in category " + category.name.getString() + " in book " + registryName);
                    }
                }
            }
            isInitialized = true;
        }
    }

    public boolean shouldSpawnWithBook() {
        return this.spawnWithBook;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("categoryList", Joiner.on(", ").join(categories))
                .append("title", title)
                .append("header", header)
                .append("itemName", itemName)
                .append("author", author)
                .append("pageTexture", pageTexture)
                .append("outlineTexture", outlineTexture)
                .append("themeColor", theme_color)
                .append("spawnWithBook", spawnWithBook)
                .append("registryName", registryName)
                .toString();
    }

}
