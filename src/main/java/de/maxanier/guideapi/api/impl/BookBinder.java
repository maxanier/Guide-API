package de.maxanier.guideapi.api.impl;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;

public class BookBinder {

    private final Identifier registryName;
    private BiConsumer<RegistryAccess,List<CategoryAbstract>> contentProvider;
    @Nonnull
    private Component guideTitle = Component.translatable("item.guideapi.book");
    @Nullable
    private Component header;
    @Nullable
    private Component itemName;
    @Nullable
    private Component author;
    private Identifier pageTexture = Identifier.fromNamespaceAndPath(GuideMod.ID, "textures/gui/book_colored.png");
    private Identifier outlineTexture = Identifier.fromNamespaceAndPath(GuideMod.ID, "textures/gui/book_greyscale.png");
    private boolean spawnWithBook;
    private int themeColor = ARGB.color(171, 70, 30);
    private int textColor = ARGB.color(0, 0, 0);
    private int textColorHighlight = ARGB.color(206, 206, 206);

    /**
     * Creates a new {@link Book} builder which will provide a much more user-friendly interface for creating books.
     *
     * @param registryName The registry name for the book to build. Should use your modid as the domain.
     */
    public BookBinder(Identifier registryName) {
        this.registryName = registryName;
    }

    /**
     * Constructs a book from the given data. Will modify specific values if not set so they have defaults.
     *
     * @return a constructed book.
     */
    public Book build() {
        if (author == null)
            this.author = Component.literal(ModList.get().getModContainerById(registryName.getNamespace()).map(ModContainer::getModInfo).map(IModInfo::getDisplayName).orElse("Unknown"));

        if (header == null)
            this.header = guideTitle;

        if (this.itemName == null)
            this.itemName = guideTitle;

        if (contentProvider == null) {
            throw new IllegalStateException("Content supplier of book " + registryName.toString() + " must be provided");
        }

        return new Book(contentProvider, guideTitle, header, itemName, author, pageTexture, outlineTexture, spawnWithBook, registryName, themeColor, textColor, textColorHighlight);
    }

    /**
     * The author of this book. If your books are lore-heavy, using an actual author name is acceptable. If not, you can
     * just use your mod name.
     * <p>
     * By default, this uses the name of the mod container obtained from looking up the domain of {@link #registryName}.
     *
     * @param author The author of this book.
     * @return the builder instance for chaining.
     */
    public BookBinder setAuthor(Component author) {
        this.author = author;
        return this;
    }

    /**
     * Sets the color to overlay on the book model and GUI border.
     * <p>
     * By default, this is a reddish-brown color.
     *
     * @param color The color to overlay with.
     * @return the builder instance for chaining.
     */
    public BookBinder setThemeColor(int color) {
        this.themeColor = color;
        return this;
    }

    public BookBinder setTextColor(int color, int colorHighlighted) {
        this.textColor = color;
        this.textColorHighlight = colorHighlighted;
        return this;
    }

    /**
     * Set a consumer (method) that will generate the content for your book and add it to the provided list
     * This will be called on client side when the book is opened for the first time.
     *
     * @param contentProvider The consumer. Categories are displayed in which they are added to the provided list
     * @return the builder instance for chaining.
     */
    public BookBinder setContentProvider(BiConsumer<RegistryAccess,List<CategoryAbstract>> contentProvider) {
        this.contentProvider = contentProvider;
        return this;
    }


    /**
     * Sets the title of this book to be displayed in the GUI.
     *
     * @param guideTitle The title of this guide.
     * @return the builder instance for chaining.
     */
    public BookBinder setGuideTitle(Component guideTitle) {
        this.guideTitle = guideTitle;
        return this;
    }

    /**
     * Sets the title of this book to be displayed in the GUI.
     *
     * @param translationKey The translation key for the title of this guide.
     * @return the builder instance for chaining.
     */
    public BookBinder setGuideTitleKey(String translationKey) {
        return this.setGuideTitle(Component.translatable(translationKey));
    }

    /**
     * Sets the header text of this book. The header is displayed at the top of the home page above the category listing.
     * <p>
     * By default, this is the same as {@link #guideTitle}.
     *
     * @param header The header text to display.
     * @return the builder instance for chaining.
     */
    public BookBinder setHeader(Component header) {
        this.header = header;
        return this;
    }

    /**
     * Sets the header text of this book. The header is displayed at the top of the home page above the category listing.
     * <p>
     * By default, this is the same as {@link #guideTitle}.
     *
     * @param translationKey The translation key for the header text to display.
     * @return the builder instance for chaining.
     */
    public BookBinder setHeaderKey(String translationKey) {
        return this.setHeader(Component.translatable(translationKey));
    }

    /**
     * Sets the unlocalized name for the item containing this book.
     * <p>
     * By default, this is the same as {@link #guideTitle}.
     *
     * @param itemName The name for this item.
     * @return the builder instance for chaining.
     */
    public BookBinder setItemName(Component itemName) {
        this.itemName = itemName;
        return this;
    }

    /**
     * Sets the unlocalized name for the item containing this book.
     * <p>
     * By default, this is the same as {@link #guideTitle}.
     *
     * @param translationKey The translation key for the name for this item.
     * @return the builder instance for chaining.
     */
    public BookBinder setItemNameKey(String translationKey) {
        return this.setItemName(Component.translatable(translationKey));
    }

    /**
     * The texture to use for the border of the book. These are colored with {@link #setThemeColor(int)}. The dimensions should remain
     * the same as the default texture.
     * <p>
     * By default, this uses a greyscale version of the outline of vanilla books.
     *
     * @param outlineTexture The outline texture to use for this guide.
     * @return the builder instance for chaining.
     */
    public BookBinder setOutlineTexture(Identifier outlineTexture) {
        this.outlineTexture = outlineTexture;
        return this;
    }

    /**
     * The texture to use for the pages themselves. These are un-colored and drawn just how they appear in the texture file.
     * The dimensions should remain the same as the default texture.
     * <p>
     * By default, this uses the same page texture as vanilla books.
     *
     * @param pageTexture The page texture to use for this guide.
     * @return the builder instance for chaining.
     */
    public BookBinder setPageTexture(Identifier pageTexture) {
        this.pageTexture = pageTexture;
        return this;
    }

    /**
     * Sets the default config option for whether new players should spawn with this book in their inventory. Players may
     * override this in the config if they wish.
     * <p>
     * By default, books will not spawn in the player's inventory.
     *
     * @return the builder instance for chaining.
     */
    public BookBinder setSpawnWithBook() {
        this.spawnWithBook = true;
        return this;
    }
}
