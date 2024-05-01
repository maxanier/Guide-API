package de.maxanier.guideapi.proxy;

import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Optional;

public class CommonProxy {


    public void initColors() {
    }

    public void openEntry(Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, Player player, ItemStack stack) {
    }

    public void openGuidebook(Player player, Level world, Book book, ItemStack bookStack) {
    }

    public void playSound(SoundEvent sound) {
    }

    public Optional<PotionBrewing> getPotionBrewing(){
        MinecraftServer server =  ServerLifecycleHooks.getCurrentServer();
        if(server != null){
            return Optional.of(server.potionBrewing());
        }
        return Optional.empty();
    }
}
