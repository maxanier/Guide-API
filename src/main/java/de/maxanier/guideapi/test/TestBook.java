package de.maxanier.guideapi.test;

import com.google.common.collect.Lists;
import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideBook;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.book.BookBinder;
import de.maxanier.guideapi.api.book.IBookContentCollector;
import de.maxanier.guideapi.api.book.IGuideBook;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.category.CategoryItemStack;
import de.maxanier.guideapi.api.entry.Entry;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.entry.EntryItemStack;
import de.maxanier.guideapi.api.pages.*;
import de.maxanier.guideapi.api.util.PageHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GuideBook
public class TestBook implements IGuideBook {

    public final static Identifier ID = Identifier.fromNamespaceAndPath(GuideMod.ID, "test_book");
    public static Book book;

    @Nullable
    @Override
    public Book buildBook() {
        BookBinder binder = new BookBinder(ID);
        binder.setAuthor(Component.literal("TehNut")).setThemeColor(Color.PINK.getRGB()).setItemName(Component.literal("Display Name")).setHeader(Component.literal("Hello there")).setGuideTitle(Component.literal("Title message")).setSpawnWithBook().setContentProvider(this::buildContent);
        return (book = binder.build());
    }

    private void buildContent(RegistryAccess registryAccess, IBookContentCollector bookContentCollector) {
        List<CategoryBase> categories = new ArrayList<>();
        Map<Identifier, EntryBase> entries = new HashMap<>();

        List<IPage> pages = Lists.newArrayList();
        pages.add(new PageText(Component.literal("Hello, this is\nsome text with a new line.")));
        pages.add(new PageText(Component.literal("Hello, this is some text without a new line. It is long so it should probably be automatically wrapped")));
        pages.addAll(PageHelper.pagesForLongText(Component.literal("Hello, this is some text. It is very long so it should be split across multiple pages. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.")));
        pages.addAll(PageHelper.pagesForLongText(Component.literal("Hello, this is some text. It is very long so it should be split across multiple pages. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."), Items.COAL_BLOCK));

        pages.add(new PageRecipe(Identifier.withDefaultNamespace("spruce_button")));
        pages.add(new PageRecipe(Identifier.withDefaultNamespace("stone")));
        pages.add(new PageRecipe(Identifier.withDefaultNamespace("charcoal")));
        pages.add(new PageRecipe(Identifier.withDefaultNamespace("invalid_recipe_that_does_not_exist")));
        pages.add(new PageRecipe(Identifier.withDefaultNamespace("stick")));
//        pages.add(new PageIRecipe(new ShapedRecipe("test", CraftingBookCategory.EQUIPMENT, new ShapedRecipePattern(1,1,NonNullList.of(Optional.of(Ingredient.of(Items.PUMPKIN))), Optional.empty()), new ItemStack(Blocks.OAK_LOG))));
        pages.add(new PageRecipe(Identifier.withDefaultNamespace("acacia_fence")));
        pages.add(new PageItemStack(Component.literal("These are all logs"), Ingredient.of(registryAccess.lookupOrThrow(Registries.ITEM).getOrThrow(ItemTags.LOGS)))); //Tags.Items.NATURAL_LOGS
        pages.add(new PageTextImage(Component.translatable("guideapi.test.string"), Identifier.fromNamespaceAndPath(GuideMod.ID, "textures/test/testimage.png"), true));
        pages.add(new PageTextImage(Component.translatable("guideapi.test.string"), Identifier.fromNamespaceAndPath(GuideMod.ID, "textures/test/testimage.png"), true, 64, 64, true));
        pages.add(new PageTextImage(Component.translatable("guideapi.test.string"), Identifier.fromNamespaceAndPath(GuideMod.ID, "textures/test/testimage.png"), false));
        pages.add(new PageTextImage(Component.translatable("guideapi.test.string"), Identifier.fromNamespaceAndPath(GuideMod.ID, "textures/test/testimage.png"), false, 64, 64, true));
        pages.add(new PageImage(Identifier.fromNamespaceAndPath(GuideMod.ID, "textures/test/testimage.png")));
        pages.add(new PageImage(Identifier.fromNamespaceAndPath(GuideMod.ID, "textures/test/testimage.png"), 64, 64, true));
        pages.add(new PageEntity(EntityType.BLAZE));
        pages.add(new PageEntity((world, reason) -> {
            Zombie z = EntityType.ZOMBIE.create(world, reason);
            z.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
            return z;
        }, Component.literal("This is a zombie")));


        Entry entry = new EntryItemStack(pages, Component.translatable("guideapi.test.entry"), new ItemStack(Items.POTATO));
        entries.put(Identifier.fromNamespaceAndPath(GuideMod.ID, "entry"), entry);
        categories.add(new CategoryItemStack(entries, Component.translatable("guideapi.test.category"), new ItemStack(Items.ACACIA_DOOR)));
        categories.add(new CategoryItemStack(entries, Component.translatable("guideapi.test.category"), new ItemStack(Items.PUMPKIN)));
        categories.add(new CategoryItemStack(entries, Component.translatable("guideapi.test.category"), new ItemStack(Items.WOODEN_AXE)));
        categories.add(new CategoryItemStack(entries, Component.translatable("guideapi.test.category"), new ItemStack(Items.SPRUCE_WOOD)));
        categories.add(new CategoryItemStack(entries, Component.translatable("guideapi.test.category"), new ItemStack(Items.BONE_MEAL)));
        categories.add(new CategoryItemStack(entries, Component.translatable("guideapi.test.category"), new ItemStack(Items.WHEAT)));

        bookContentCollector.addCategories(categories);
    }
}
