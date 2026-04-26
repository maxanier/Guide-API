package de.maxanier.guideapi.api.category;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.entry.EntryBase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;


import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class CategoryBase {

    public final Map<Identifier, EntryBase> entries;
    public final Component name;
    private String keyBase;

    public CategoryBase(Map<Identifier, EntryBase> entries, Component name) {
        this.entries = entries;
        this.name = name;
    }

    public CategoryBase(Component name) {
        this(Maps.newLinkedHashMap(), name);
    }

    public void addEntries(Map<Identifier, EntryBase> entries) {
        this.entries.putAll(entries);
    }

    /**
     * Adds an entry to this category.
     *
     * @param key   - The key of the entry to add.
     * @param entry - The entry to add.
     */
    public void addEntry(Identifier key, EntryBase entry) {
        entries.put(key, entry);
    }

    /**
     * Adds an entry to this category.
     * <p>
     * Shorthand of {@link #addEntry(Identifier, EntryBase)}. Requires {@link #withKeyBase(String)} to have been called.
     *
     * @param key   - The key of the entry to add.
     * @param entry - The entry to add.
     */
    public void addEntry(String key, EntryBase entry) {
        if (Strings.isNullOrEmpty(keyBase))
            throw new RuntimeException("keyBase in category with name '" + name + "' must be set.");

        addEntry(Identifier.fromNamespaceAndPath(keyBase, key), entry);
    }

    public abstract boolean canSee(Player player, Book book);

    public abstract void draw(GuiGraphics graphics, Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX, int mouseY, GuideBookScreen screen, boolean drawOnLeft);

    public abstract void drawExtras(GuiGraphics graphics, Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX, int mouseY, GuideBookScreen screen, boolean drawOnLeft);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryBase that = (CategoryBase) o;
        if (!Objects.equals(entries, that.entries)) return false;
        return Objects.equals(name, that.name);
    }

    /**
     * Obtains an entry from this category.
     * <p>
     * This <i>can</i> be null, however it is not marked as nullable to avoid annoying IDE warnings. I am making the
     * assumption that this will only be called while creating the book and thus the caller knows it exists.
     * <p>
     * If you are calling at any other time, make sure to nullcheck this.
     *
     * @param key - The key of the entry to obtain.
     * @return the found entry.
     */
    public EntryBase getEntry(Identifier key) {
        return entries.get(key);
    }

    /**
     * Obtains an entry from this category.
     * <p>
     * Shorthand of {@link #getEntry(Identifier)}. Requires {@link #withKeyBase(String)} to have been called.
     * <p>
     * This <i>can</i> be null, however it is not marked as nullable to avoid annoying IDE warnings. I am making the
     * assumption that this will only be called while creating the book and thus the caller knows it exists.
     * <p>
     * If you are calling at any other time, make sure to nullcheck this.
     *
     * @param key - The key of the entry to obtain.
     * @return the found entry.
     */
    public EntryBase getEntry(String key) {
        if (Strings.isNullOrEmpty(keyBase))
            throw new RuntimeException("keyBase in category with name '" + name.getString() + "' must be set.");

        return getEntry(Identifier.fromNamespaceAndPath(keyBase, key));
    }

    /**
     * Obtains a localized copy of this category's name.
     *
     * @return a localized copy of this category's name.
     */
    public Component getName() {
        return name;
    }

    public List<Component> getTooltip() {
        return Lists.newArrayList(getName());
    }

    @Override
    public int hashCode() {
        int result = entries != null ? entries.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public abstract void onInit(Book book, Player player);

    public abstract void onLeftClicked(Book book, double mouseX, double mouseY, Player player);


    public abstract void onRightClicked(Book book, double mouseX, double mouseY, Player player);

    public void removeEntries(List<Identifier> keys) {
        for (Identifier key : keys)
            entries.remove(key);
    }

    public void removeEntry(Identifier key) {
        entries.remove(key);
    }

    /**
     * Sets the domain to use for all Identifier keys passed through {@link #getEntry(String)} and
     * {@link #addEntry(String, EntryBase)}
     * <p>
     * Required in order to use those.
     *
     * @param keyBase - The base domain for this entry.
     * @return self for chaining.
     */
    public CategoryBase withKeyBase(String keyBase) {
        this.keyBase = keyBase;
        return this;
    }
}
