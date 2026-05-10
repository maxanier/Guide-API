package de.maxanier.guideapi.api.book;


import de.maxanier.guideapi.api.category.CategoryBase;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

/**
 * Accepts book content
 */
public interface IBookContentCollector {
    /**
     * Add links between blocks in the world and book entries
     *
     * @param linkedEntries Map from block ids to entry ids
     */
    void addBlockLinkedEntries(Map<Identifier, Identifier> linkedEntries);

    /**
     * Categories are displayed in which they are included in the list
     *
     * @param categories Complete categories with entries included
     */
    void addCategories(List<CategoryBase> categories);
}