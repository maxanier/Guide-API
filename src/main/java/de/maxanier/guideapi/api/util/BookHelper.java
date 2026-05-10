package de.maxanier.guideapi.api.util;

import com.google.common.collect.Lists;
import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.book.IBookContentCollector;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.pages.IPage;
import de.maxanier.guideapi.api.pages.PageHolderWithLinks;
import de.maxanier.guideapi.api.pages.PageRecipe;
import de.maxanier.guideapi.api.recipes.IRecipeRenderer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Assists with book content creation and allows using {@link ItemInfoBuilder}.
 * Create one instant for your book using the {@link BookHelper.Builder}.
 * <p>
 * If you want to create links between entries, add links to pages with {@link BookHelper#addLinks(List, Object...)} and register your entries with {@link BookHelper#registerLinkablePages(List)}.
 */
public class BookHelper {

    private final Logger LOGGER;
    private final RegistryAccess registryAccess;
    private final String modid;
    private final String baseKey;
    private final Function<RecipeHolder<?>, IRecipeRenderer> recipeRendererSupplier;
    private final BiFunction<String, Object[], Component> localizer;
    private final Function<Block, String> blockNameMapper;
    private final Function<Item, String> itemNameMapper;
    private final Map<Identifier, EntryBase> links = new HashMap<>();
    private final Map<Identifier, Identifier> blockLinks = new HashMap<>();

    private BookHelper(RegistryAccess registryAccess, String modid, String baseKey, Function<RecipeHolder<?>, IRecipeRenderer> recipeRendererSupplier, BiFunction<String, Object[], Component> localizer, Function<Block, String> blockNameMapper, Function<Item, String> itemNameMapper) {
        LOGGER = LogManager.getLogger("BookHelper_" + modid);
        this.registryAccess = registryAccess;
        this.modid = modid;
        this.baseKey = baseKey;
        this.recipeRendererSupplier = recipeRendererSupplier;
        this.localizer = localizer;
        this.blockNameMapper = blockNameMapper;
        this.itemNameMapper = itemNameMapper;
    }

    /**
     * Converts the given pages to {@link PageHolderWithLinks} and adds the given links
     * Links can be
     * - Identifier: Link to an entry by its id. Entries have to be registered by {@link BookHelper#registerLinkablePages(List)}
     * - EntryAbstract: Link directly to an entry object
     * - {@link PageHolderWithLinks.URLLink}: Link to a web url
     *
     * @return The SAME list with the old pages removed and the new page holders added
     */
    public List<IPage> addLinks(List<IPage> pages, Object... links) {
        List<PageHolderWithLinks> linkPages = Lists.newArrayList();
        for (IPage p : pages) {
            linkPages.add(new PageHolderWithLinks(this, p));
        }

        for (Object l : links) {
            switch (l) {
                case Identifier resourceLocation -> {
                    for (PageHolderWithLinks p : linkPages) {
                        p.addLink(resourceLocation);
                    }
                }
                case EntryBase entryAbstract -> {
                    for (PageHolderWithLinks p : linkPages) {
                        p.addLink(entryAbstract);
                    }
                }
                case PageHolderWithLinks.URLLink urlLink -> {
                    for (PageHolderWithLinks p : linkPages) {
                        p.addLink(urlLink);
                    }
                }
                case null, default -> LOGGER.warn("Given link object cannot be linked {}", l);
            }
        }
        pages.clear();
        pages.addAll(linkPages);
        return pages;
    }

    /**
     * Add a link between block and entry in the book
     * Don't forget to call {@link BookHelper#registerBlockLinkedEntries(IBookContentCollector)} at the end
     */
    public void addBlockLink(Identifier blockId, Identifier entryId) {
        this.blockLinks.put(blockId, entryId);
    }

    /**
     * Cycles through all the matching item stacks
     *
     * @param mainStack is used to determine the translation keys.
     * @return A ItemInfoBuilder for the given itemsstacks.
     */
    public ItemInfoBuilder info(Ingredient ingredient, ItemStack mainStack) {
        Item item = mainStack.getItem();
        String name = itemNameMapper.apply(item);
        return new ItemInfoBuilder(this, ingredient, mainStack, name, false);
    }

    /**
     * Return a brewing recipe that results in the given stack
     *
     * @return Null if none found
     */
    @Nullable
    public BrewingRecipe getBrewingRecipe(ItemStack stack) {
        return (BrewingRecipe) GuideMod.PROXY.getPotionBrewing().map(PotionBrewing::getRecipes).map(Collection::stream).flatMap(s -> s.filter(iBrewingRecipe -> iBrewingRecipe instanceof BrewingRecipe && ItemStack.matches(((BrewingRecipe) iBrewingRecipe).getOutput(), stack)).findFirst()).orElse(null);
    }

    @Nullable
    public EntryBase getLinkedEntry(Identifier location) {
        return links.get(location);
    }

    public IPage getRecipePage(Identifier id) {
        return new PageRecipe(id, recipeRendererSupplier);
    }

    public ItemInfoBuilder info(TagKey<Item> itemTag, ItemLike mainItem) {
        Ingredient i = Ingredient.of(registryAccess.lookupOrThrow(Registries.ITEM).getOrThrow(itemTag));
        return info(i, new ItemStack(mainItem));
    }

    public ItemInfoBuilder infoBlock(Ingredient ingredient, Block mainBlock) {
        String name = blockNameMapper.apply(mainBlock);
        return new ItemInfoBuilder(this, ingredient, new ItemStack(mainBlock), name, true);
    }

    public ItemInfoBuilder infoBlocks(TagKey<Item> blockItemTag, Block mainBlock) {
        Ingredient i = Ingredient.of(registryAccess.lookupOrThrow(Registries.ITEM).getOrThrow(blockItemTag));
        return infoBlock(i, mainBlock);
    }

    /**
     * The first block is used to determine the translation keys.
     *
     * @param blocks The resulting page will cycle through the blocks in the given order
     * @return A ItemInfoBuilder for the given blocks.
     */
    public ItemInfoBuilder infoBlocks(Block... blocks) {
        assert blocks.length > 0;
        Block i0 = blocks[0];
        String name = blockNameMapper.apply(i0);
        return new ItemInfoBuilder(this, Ingredient.of(blocks), new ItemStack(i0), name, true);
    }

    /**
     * The first item is used to determine the translation keys.
     *
     * @param items The resulting page will cycle through the items in the given order
     * @return A ItemInfoBuilder for the given items.
     */
    public ItemInfoBuilder info(Item... items) {
        assert items.length > 0;
        Item i0 = items[0];
        String name = itemNameMapper.apply(i0);
        return new ItemInfoBuilder(this, Ingredient.of(items), new ItemStack(i0), name, false);
    }

    public ItemInfoBuilder info(boolean block, ItemStack... stacks) {
        assert stacks.length > 0;
        ItemStack demoStack;
        Ingredient ingredient;
        if (stacks.length == 1) {
            demoStack = stacks[0];
            ingredient = DataComponentIngredient.of(false, demoStack);
        } else {
            demoStack = stacks[0];
            ingredient = CompoundIngredient.of(Arrays.stream(stacks).map(s -> DataComponentIngredient.of(false, s)).toArray(Ingredient[]::new));
        }
        Item item = demoStack.getItem();
        String name = item instanceof BlockItem ? blockNameMapper.apply(((BlockItem) item).getBlock()) : itemNameMapper.apply(item);
        return new ItemInfoBuilder(this, ingredient, demoStack, name, block);
    }

    /**
     * Add all collected block linked entries to the given book
     */
    public void registerBlockLinkedEntries(IBookContentCollector collector) {
        collector.addBlockLinkedEntries(this.blockLinks);
    }

    public Component localize(String key, Object... formats) {
        return localizer.apply(key, formats);
    }

    /**
     * After building your categories register them here, so the links for the individual pages can be resolved
     */
    public void registerLinkablePages(List<CategoryBase> categories) {
        for (CategoryBase c : categories) {
            this.links.putAll(c.entries);
        }
    }

    protected String getBaseKey() {
        return baseKey;
    }

    protected String getModid() {
        return modid;
    }

    public static class Builder {
        private final String modid;
        private String baseKey;
        private Function<RecipeHolder<?>, IRecipeRenderer> recipeRendererSupplier = PageRecipe::createRenderer;
        private BiFunction<String, Object[], Component> localizer = Component::translatable;
        private Function<Block, String> blockNameMapper = (block -> BuiltInRegistries.BLOCK.getKey(block).getPath());
        private Function<Item, String> itemNameMapper = (item -> BuiltInRegistries.ITEM.getKey(item).getPath());

        public Builder(String modid) {
            this.modid = modid;
            this.baseKey = "guide." + modid;
        }

        public BookHelper build(RegistryAccess access) {
            return new BookHelper(access, modid, baseKey, recipeRendererSupplier, localizer, blockNameMapper, itemNameMapper);
        }

        /**
         * Set a base key from which the item/block description translation keys are derived
         *
         * @return this
         */
        public BookHelper.Builder setBaseKey(String baseKey) {
            this.baseKey = baseKey;
            return this;
        }

        /**
         * Set a custom block -> name mapper here. The name is used as part of the translations key.
         * By default the registry name path is used.
         * Can be interesting if you for example have a lot of blocks that exist in different variants that should be treated as one in the guide, but implement a common interface.
         * Otherwise you can always use {@link ItemInfoBuilder#setKeyName(String)}
         *
         * @return this
         */
        public Builder setBlockNameMapper(Function<Block, String> blockNameMapper) {
            this.blockNameMapper = blockNameMapper;
            return this;
        }

        /**
         * Set a custom block -> name mapper here. The name is used as part of the translations key.
         * By default the registry name path is used.
         * Can be interesting if you for example have a lot of items that exist in different variants that should be treated as one in the guide, but implement a common interface.
         * Otherwise you can always use {@link ItemInfoBuilder#setKeyName(String)}
         *
         * @return this
         */
        public Builder setItemNameMapper(Function<Item, String> itemNameMapper) {
            this.itemNameMapper = itemNameMapper;
            return this;
        }

        /**
         * Set a custom method used to localize strings instead of {@link Component#translatable(String)}
         *
         * @param localizer Accept translation key and formats
         * @return this
         */
        public BookHelper.Builder setLocalizer(BiFunction<String, Object[], Component> localizer) {
            this.localizer = localizer;
            return this;
        }

        /**
         * If you have custom recipe renderers, register a render supplier here
         *
         * @param rendererSupplier Should provide a recipe renderer for any used recipe
         * @return this
         */
        public BookHelper.Builder setRecipeRendererSupplier(Function<RecipeHolder<?>, IRecipeRenderer> rendererSupplier) {
            this.recipeRendererSupplier = rendererSupplier;
            return this;
        }

    }


}
