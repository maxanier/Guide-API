package de.maxanier.guideapi.api.util;

import de.maxanier.guideapi.LogHelper;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.entry.EntryItemStack;
import de.maxanier.guideapi.api.pages.IPage;
import de.maxanier.guideapi.api.pages.PageBrewingRecipe;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class to build item or block info pages.
 * Create a {@link BookHelper} for your guide to use this.
 */
public class ItemInfoBuilder {


    private final boolean block;
    private final BookHelper bookHelper;
    private final Ingredient ingredient;
    private final ItemStack mainStack;
    private final List<IPage> additionalPages = new ArrayList<>();
    private String name;
    private Object[] formats = new Object[0];
    private Object[] links = null;
    private boolean customName;
    @Nonnull
    private List<Identifier> recipes = Collections.emptyList();
    @Nullable
    private ItemStack[] brewingStacks;

    /**
     * @param name       name used for translation keys
     * @param ingredient The relevant item stack. Used for display and strings.
     * @param block      Whether this entry is about a block
     */
    protected ItemInfoBuilder(BookHelper bookHelper, Ingredient ingredient, ItemStack mainStack, String name, boolean block) {
        this.ingredient = ingredient;
        this.block = block;
        this.mainStack = mainStack;
        this.name = name;
        this.bookHelper = bookHelper;
    }

    /**
     * Add items that can be created in a brewing stand
     *
     * @return this
     */
    public ItemInfoBuilder brewingItems(Item... brewableItems) {
        this.brewingStacks = Arrays.stream(brewableItems).map(ItemStack::new).toArray(ItemStack[]::new);
        return this;
    }

    /**
     * Add stacks that can be created in a brewing stand
     *
     * @return this
     */
    public ItemInfoBuilder brewingStacks(ItemStack... brewableStacks) {
        this.brewingStacks = brewableStacks;
        return this;
    }

    /**
     * Builds the entry and adds it to the given map
     */
    public void build(Map<Identifier, EntryBase> entries) {
        String base = bookHelper.getBaseKey() + (block ? ".blocks" : ".items") + "." + name;
        ArrayList<IPage> pages = new ArrayList<>(PageHelper.pagesForLongText(bookHelper.localize(base + ".text", formats), ingredient));
        for (Identifier id : recipes) {
            pages.add(bookHelper.getRecipePage(id));
        }
        if (brewingStacks != null) {
            for (ItemStack brew : brewingStacks) {
                BrewingRecipe r = bookHelper.getBrewingRecipe(brew);
                if (r == null) {
                    LogHelper.info("Could not find brewing recipe for " + brew.toString());
                } else {
                    pages.add(new PageBrewingRecipe(r));
                }
            }
        }
        pages.addAll(this.additionalPages);
        if (links != null) bookHelper.addLinks(pages, links);
        Identifier entryId = Identifier.fromNamespaceAndPath(this.bookHelper.getModid(), base);
        if (block) {
            ingredient.getValues().stream().map(Holder::value).filter(item -> item instanceof BlockItem).map(item -> ((BlockItem) item).getBlock()).map(Block::builtInRegistryHolder).map(Holder.Reference::getKey).filter(Objects::nonNull).forEach(blockId -> bookHelper.addBlockLink(blockId.identifier(), entryId));
        }
        entries.put(entryId, new EntryItemStack(pages, customName ? Component.translatable(base) : mainStack.getItemName(), mainStack));
    }

    /**
     * Add pages that are placed at the end of the entry
     */
    public ItemInfoBuilder customPages(IPage... additionalPages) {
        this.additionalPages.addAll(Arrays.asList(additionalPages));
        return this;
    }

    /**
     * Add recipes
     * String ids are prefixed with your modid
     *
     * @param modIDs without namespace prefix
     * @return this
     */
    public ItemInfoBuilder recipes(String... modIDs) {
        this.recipes = Arrays.stream(modIDs).map(id -> Identifier.fromNamespaceAndPath(bookHelper.getModid(), id)).collect(Collectors.toList());
        return this;
    }

    /**
     * Add recipes
     *
     * @param ids the ids of the recipes to be displayeed
     */
    public ItemInfoBuilder recipes(Identifier... ids) {
        this.recipes = Arrays.asList(ids);
        return this;
    }

    /**
     * Adds format arguments which are used when translating the description
     */
    public ItemInfoBuilder setFormats(Object... formats) {
        this.formats = formats;
        return this;
    }

    /**
     * Set's the name used for unloc strings
     */
    public ItemInfoBuilder setKeyName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets links that are added to the description pages.
     * See {@link BookHelper#addLinks(List, Object...)}
     */
    public ItemInfoBuilder setLinks(Object... links) {
        this.links = links;
        return this;
    }

    /**
     * Use a custom name (basekey.name) instead of the translated Item/Block name for the entry title
     */
    public ItemInfoBuilder useCustomEntryName() {
        customName = true;
        return this;
    }
}
