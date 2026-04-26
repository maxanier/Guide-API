package de.maxanier.guideapi.core.proxy;

import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Optional;

public class CommonProxy {

    public RecipeMap getClientSyncedRecipes() {
        return RecipeMap.EMPTY;
    }

    public Optional<PotionBrewing> getPotionBrewing() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return Optional.of(server.potionBrewing());
        }
        return Optional.empty();
    }

    public void openEntry(Book book, CategoryBase categoryAbstract, EntryBase entryAbstract, Player player) {
    }

    public void openGuidebook(Player player, Level world, Book book, ItemStack bookStack) {
    }

    public void playSound(SoundEvent sound) {
    }
}
