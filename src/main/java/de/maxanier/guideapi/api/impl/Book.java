package de.maxanier.guideapi.api.impl;

import com.google.common.base.Joiner;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.util.LogHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Book {

    private final List<CategoryAbstract> categories = new ArrayList<>();
    private final Consumer<List<CategoryAbstract>> contentProvider;
    private final ITextComponent title;
    private final ITextComponent header;
    private final ITextComponent itemName;
    private final ITextComponent author;
    private final ResourceLocation pageTexture;
    private final ResourceLocation outlineTexture;
    private final Color color;
    private final boolean spawnWithBook;
    private final ResourceLocation registryName;
    private final ItemGroup creativeTab;
    private boolean isInitialized;


    protected Book(Consumer<List<CategoryAbstract>> contentProvider, ITextComponent title, ITextComponent header, ITextComponent displayName, ITextComponent author, ResourceLocation pageTexture, ResourceLocation outlineTexture, Color color, boolean spawnWithBook, ResourceLocation registryName, ItemGroup creativeTab) {
        this.contentProvider = contentProvider;
        this.title = title;
        this.header = header;
        this.itemName = displayName;
        this.author = author;
        this.pageTexture = pageTexture;
        this.outlineTexture = outlineTexture;
        this.color = color;
        this.spawnWithBook = spawnWithBook;
        this.registryName = registryName;
        this.creativeTab = creativeTab;
    }

    public void initializeContent() {
        if (!isInitialized) {
            LogHelper.debug("Opening book " + registryName.toString() + " for the first time -> Initializing content");
            contentProvider.accept(categories);
            isInitialized = true;
        }
    }

    /**
     * Can be used to force content initialisation independent of first use regardless of if it was initialized previously.
     * Use at own risk. Might cause crashes if the book is currently open.
     */
    public void forceInitializeContent() {
        LogHelper.info("Force initializing book content " + registryName.toString());
        categories.clear();
        isInitialized = false;
        initializeContent();
    }

    public List<CategoryAbstract> getCategoryList() {
        return this.categories;
    }

    public ITextComponent getAuthor() {
        return this.author;
    }

    public ITextComponent getHeader() {
        return this.header;
    }

    public ITextComponent getItemName() {
        return this.itemName;
    }

    public ITextComponent getTitle() {
        return this.title;
    }

    public ResourceLocation getPageTexture() {
        return this.pageTexture;
    }

    public ResourceLocation getOutlineTexture() {
        return this.outlineTexture;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean shouldSpawnWithBook() {
        return this.spawnWithBook;
    }

    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    public ItemGroup getCreativeTab() {
        return this.creativeTab;
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
                .append("color", color)
                .append("spawnWithBook", spawnWithBook)
                .append("registryName", registryName)
                .append("creativeTab", creativeTab)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return getRegistryName().equals(book.getRegistryName());

    }

    @Override
    public int hashCode() {
        return getRegistryName().hashCode();
    }
}
