package de.maxanier.guideapi.core.proxy;

import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.book.IGuideItem;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.core.client.GuideModClient;
import de.maxanier.guideapi.core.gui.CategoryScreen;
import de.maxanier.guideapi.core.gui.EntryScreen;
import de.maxanier.guideapi.core.gui.HomeScreen;
import de.maxanier.guideapi.core.item.ItemGuideBookDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ClientProxy extends CommonProxy {

    @Override
    public RecipeMap getClientSyncedRecipes() {
        return GuideModClient.getSyncedRecipes();
    }

    @Override
    public Optional<PotionBrewing> getPotionBrewing() {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            return Optional.of(level.potionBrewing());
        }
        return Optional.empty();
    }

    @Override
    public void openEntry(Book book, CategoryBase categoryAbstract, EntryBase entryAbstract, Player player) {
        Minecraft.getInstance().setScreen(new EntryScreen(book, categoryAbstract, entryAbstract, player));
    }

    @Override
    public void openGuidebook(Player player, Level world, Book book, ItemStack bookStack) {
        if (!bookStack.isEmpty() && bookStack.getItem() instanceof IGuideItem) {
            book.initializeContent(world.registryAccess());
            try {
                if (bookStack.has(ItemGuideBookDataComponents.ENTRY) && bookStack.has(ItemGuideBookDataComponents.CATEGORY)) {
                    CategoryBase category = book.getCategoryList().get(bookStack.get(ItemGuideBookDataComponents.CATEGORY));
                    EntryBase entry = category.entries.get(bookStack.get(ItemGuideBookDataComponents.ENTRY));
                    int pageNumber = bookStack.getOrDefault(ItemGuideBookDataComponents.PAGE, 0);
                    EntryScreen guiEntry = new EntryScreen(book, category, entry, player);
                    Minecraft.getInstance().setScreen(guiEntry);
                    guiEntry.setPage(pageNumber);
                    return;
                } else if (bookStack.has(ItemGuideBookDataComponents.CATEGORY)) {
                    CategoryBase category = book.getCategoryList().get(bookStack.get(ItemGuideBookDataComponents.CATEGORY));
                    int entryPage = bookStack.getOrDefault(ItemGuideBookDataComponents.PAGE, 0);
                    CategoryScreen guiCategory = new CategoryScreen(book, category, player, null);
                    Minecraft.getInstance().setScreen(guiCategory);
                    guiCategory.setPage(entryPage);
                    return;
                } else {
                    int categoryNumber = bookStack.getOrDefault(ItemGuideBookDataComponents.PAGE, 0);
                    HomeScreen guiHome = new HomeScreen(book, player);
                    guiHome.categoryPage = categoryNumber;
                    Minecraft.getInstance().setScreen(guiHome);
                    return;
                }

            } catch (Exception e) {
                // No-op: If the linked content doesn't exist anymore
            }

            Minecraft.getInstance().setScreen(new HomeScreen(book, player));
        }
    }

    @Override
    public void playSound(SoundEvent sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1));
    }
}
