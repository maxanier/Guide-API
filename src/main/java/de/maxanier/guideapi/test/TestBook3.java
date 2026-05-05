package de.maxanier.guideapi.test;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.GuideBook;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.book.BookBinder;
import de.maxanier.guideapi.api.book.IGuideBook;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.category.CategoryItemStack;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.entry.EntryItemStack;
import de.maxanier.guideapi.api.pages.IPage;
import de.maxanier.guideapi.api.pages.PageHolderWithLinks;
import de.maxanier.guideapi.api.util.BookHelper;
import de.maxanier.guideapi.api.util.ItemInfoBuilder;
import de.maxanier.guideapi.api.util.PageHelper;
import de.maxanier.guideapi.api.world.IInfoRenderer;
import de.maxanier.guideapi.api.world.InfoRendererDescription;
import de.maxanier.guideapi.api.world.InfoRendererImage;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Use {@link BookHelper} , {@link ItemInfoBuilder} and {@link IInfoRenderer}
 */
@GuideBook
public class TestBook3 implements IGuideBook {

    public final static Identifier ID = Identifier.fromNamespaceAndPath(GuideMod.ID, "test_book3");
    public static Book book;

    @Nullable
    @Override
    public Book buildBook() {
        BookBinder binder = new BookBinder(ID);
        binder.setAuthor(Component.literal("Maxanier")).setThemeColor(ARGB.color(80, 50, 5)).setItemName(Component.literal("Display Name")).setHeader(Component.literal("Hello there")).setSpawnWithBook().setGuideTitle(Component.literal("Title message")).setContentProvider(this::buildContent);
        book = binder.build();
        return book;
    }

    @Override
    public void registerInfoRenderer(Book yourBook) {
        GuideAPI.registerInfoRenderer(yourBook, new InfoRendererDescription(new ItemStack(Blocks.COAL_BLOCK), Component.translatable("guideapi.test.blocks.compressed_blocks.hint")).setTiny(true), Blocks.COAL_BLOCK, Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK);
        GuideAPI.registerInfoRenderer(yourBook, new InfoRendererImage(Identifier.fromNamespaceAndPath(GuideMod.ID, "textures/test/testimage.png"), 64, 64), BlockTags.WOOL);
    }

    private void buildContent(RegistryAccess registryAccess, List<CategoryBase> categories) {
        BookHelper helper = new BookHelper.Builder(GuideMod.ID).setBaseKey("guideapi.test").build();

        CategoryBase blocks = new CategoryItemStack(Component.literal("Blocks"), new ItemStack(Blocks.STONE)).withKeyBase(GuideMod.ID);
        Map<Identifier, EntryBase> blockEntries = new LinkedHashMap<>();
        helper.info(Blocks.COAL_BLOCK, Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK).recipes(Identifier.fromNamespaceAndPath("minecraft", "coal_block"), Identifier.withDefaultNamespace("iron_block"), Identifier.withDefaultNamespace("gold_block")).useCustomEntryName().setKeyName("compressed_blocks").setLinks(Identifier.fromNamespaceAndPath(GuideMod.ID, "guideapi.test.items.ingots")).setFormats(9).build(blockEntries);
        blocks.addEntries(blockEntries);
        categories.add(blocks);

        CategoryBase items = new CategoryItemStack(Component.literal("Items"), new ItemStack(Items.IRON_AXE)).withKeyBase(GuideMod.ID);
        Map<Identifier, EntryBase> itemEntries = new LinkedHashMap<>();
        helper.info(Items.APPLE).brewingStacks().build(itemEntries);
        helper.info(false, Ingredient.of(registryAccess.lookupOrThrow(Registries.ITEM).getOrThrow(ItemTags.WOOL)), new ItemStack(Items.IRON_INGOT)).useCustomEntryName().recipes(Identifier.withDefaultNamespace("iron_ingot_from_nuggets"), Identifier.withDefaultNamespace("gold_ingot_from_nuggets")).setKeyName("ingots").setLinks(Identifier.fromNamespaceAndPath(GuideMod.ID, "guideapi.test.blocks.compressed_blocks")).build(itemEntries);
        List<IPage> troublePages = new ArrayList<>();
        troublePages.addAll(PageHelper.pagesForLongText(Component.translatable("guideapi.test.entry")));
        helper.addLinks(troublePages, new PageHolderWithLinks.URLLink(Component.literal("Troubleshooting"), URI.create("github.com/maxanier/Guide-API")), blockEntries.values().stream().findFirst().orElse(null));
        itemEntries.put(Identifier.fromNamespaceAndPath(GuideMod.ID, "linktest"), new EntryItemStack(troublePages, Component.literal("Link"), new ItemStack(Items.BOOK)));

        items.addEntries(itemEntries);
        categories.add(items);

        helper.registerLinkablePages(categories);
    }
}
