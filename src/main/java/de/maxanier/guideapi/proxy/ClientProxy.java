package de.maxanier.guideapi.proxy;

import de.maxanier.guideapi.RegistrarGuideAPIClient;
import de.maxanier.guideapi.api.BookEvent;
import de.maxanier.guideapi.api.IGuideItem;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.gui.CategoryScreen;
import de.maxanier.guideapi.gui.EntryScreen;
import de.maxanier.guideapi.gui.HomeScreen;
import de.maxanier.guideapi.item.ItemGuideBookDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Optional;

public class ClientProxy extends CommonProxy {

    @Override
    public void openEntry(Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, Player player, ItemStack stack) {
        BookEvent.Open event = new BookEvent.Open(book, stack, player);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            player.displayClientMessage(event.getCanceledText(), true);
            return;
        }

        Minecraft.getInstance().setScreen(new EntryScreen(book, categoryAbstract, entryAbstract, player, stack));
    }

    @Override
    public void openGuidebook(Player player, Level world, Book book, ItemStack bookStack) {
        if (!bookStack.isEmpty() && bookStack.getItem() instanceof IGuideItem) {
            book.initializeContent(world.registryAccess());
            try {
                    if (bookStack.has(ItemGuideBookDataComponents.ENTRY) && bookStack.has(ItemGuideBookDataComponents.CATEGORY)) {
                        CategoryAbstract category = book.getCategoryList().get(bookStack.get(ItemGuideBookDataComponents.CATEGORY));
                        EntryAbstract entry = category.entries.get(bookStack.get(ItemGuideBookDataComponents.ENTRY));
                        int pageNumber = bookStack.getOrDefault(ItemGuideBookDataComponents.PAGE,0);
                        EntryScreen guiEntry = new EntryScreen(book, category, entry, player, bookStack);
                        Minecraft.getInstance().setScreen(guiEntry);
                        guiEntry.setPage(pageNumber);
                        return;
                    } else if (bookStack.has(ItemGuideBookDataComponents.CATEGORY)) {
                        CategoryAbstract category = book.getCategoryList().get(bookStack.get(ItemGuideBookDataComponents.CATEGORY));
                        int entryPage = bookStack.getOrDefault(ItemGuideBookDataComponents.PAGE,0);
                        CategoryScreen guiCategory = new CategoryScreen(book, category, player, bookStack, null);
                        Minecraft.getInstance().setScreen(guiCategory);
                        guiCategory.setPage(entryPage);
                        return;
                    } else {
                        int categoryNumber = bookStack.getOrDefault(ItemGuideBookDataComponents.PAGE,0);
                        HomeScreen guiHome = new HomeScreen(book, player, bookStack);
                        guiHome.categoryPage = categoryNumber;
                        Minecraft.getInstance().setScreen(guiHome);
                        return;
                    }

            } catch (Exception e) {
                // No-op: If the linked content doesn't exist anymore
            }

            Minecraft.getInstance().setScreen(new HomeScreen(book, player, bookStack));
        }
    }

    @Override
    public void playSound(SoundEvent sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1));
    }

    @Override
    public Optional<PotionBrewing> getPotionBrewing() {
        Level level = Minecraft.getInstance().level;
        if(level != null){
            return Optional.of(level.potionBrewing());
        }
        return Optional.empty();
    }

    @Override
    public RecipeMap getClientSyncedRecipes() {
        return RegistrarGuideAPIClient.getSyncedRecipes();
    }
}
