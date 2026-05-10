package de.maxanier.guideapi.test;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideBook;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.book.BookBinder;
import de.maxanier.guideapi.api.book.IBookContentCollector;
import de.maxanier.guideapi.api.book.IGuideBook;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.category.CategoryItemStack;
import de.maxanier.guideapi.api.entry.EntryItemStack;
import de.maxanier.guideapi.api.pages.PageBrewingRecipe;
import de.maxanier.guideapi.api.pages.PageRecipe;
import de.maxanier.guideapi.api.pages.PageText;
import de.maxanier.guideapi.api.util.PageHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@GuideBook
public class TestBook2 implements IGuideBook {

    public final static Identifier ID = Identifier.fromNamespaceAndPath(GuideMod.ID, "test_book2");
    public static Book book;

    @Nullable
    @Override
    public Book buildBook() {
        BookBinder binder = new BookBinder(ID);
        binder.setAuthor(Component.literal("TehNut")).setThemeColor(ARGB.color(80, 50, 5)).setTextColor(ARGB.color(70, 20, 20), ARGB.color(160, 60, 60)).setItemName(Component.literal("Display Name")).setHeader(Component.literal("Hello there")).setSpawnWithBook().setGuideTitle(Component.literal("Title message")).setContentProvider(this::buildContent);


        return (book = binder.build());
    }

    private void buildContent(RegistryAccess registryAccess, IBookContentCollector contentCollector) {
        List<CategoryBase> categories = new ArrayList<>();
        CategoryBase testCategory = new CategoryItemStack(Component.translatable("guideapi.test.category"), new ItemStack(Items.BLUE_BANNER)).withKeyBase("guideapi");
        testCategory.addEntry("entry", new EntryItemStack(Component.translatable("guideapi.test.entry"), new ItemStack(Items.POTATO)));
        testCategory.getEntry("entry").addPage(new PageText(Component.literal("Hello, this is\nsome text")));
        //testCategory.getEntry("entry").addPage(new PageFurnaceRecipe(Blocks.COBBLESTONE));
        //testCategory.getEntry("entry").addPage(PageIRecipe.newShaped(new ItemStack(Items.ACACIA_BOAT), "X X", "XXX", 'X', new ItemStack(Blocks.ACACIA_PLANKS, 1, 4)));
        testCategory.addEntry("entry2", new EntryItemStack(Component.translatable("guideapi.test.entry"), new ItemStack(Items.TNT_MINECART)));
        testCategory.getEntry("entry2").addPage(new PageText(Component.literal("Hello, this is\nsome text")));
        testCategory.getEntry("entry2").addPage(new PageBrewingRecipe(new BrewingRecipe(Ingredient.of(Items.POTION), Ingredient.of(Items.GLISTERING_MELON_SLICE), PotionContents.createItemStack(Items.POTION, Potions.HEALING))));
        testCategory.getEntry("entry").addPage(new PageRecipe(Identifier.withDefaultNamespace("bread")));
        testCategory.getEntry("entry").addPage(new PageRecipe(Identifier.withDefaultNamespace("redstone")));
        testCategory.getEntry("entry").addPageList(PageHelper.pagesForLongText(Component.translatable("guideapi.test.format")));
        testCategory.addEntry("unicode", new EntryItemStack(Component.literal("Творческая книга"), new ItemStack(Items.BEEF)));
        testCategory.getEntry("unicode").addPage(new PageText(Component.literal("Творческая книга \u0F06")));
        categories.add(testCategory);

        contentCollector.addCategories(categories);
    }
}
