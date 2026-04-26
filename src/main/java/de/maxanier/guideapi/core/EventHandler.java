package de.maxanier.guideapi.core;

import de.maxanier.guideapi.GuideConfig;
import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.book.Book;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = GuideMod.ID)
public class EventHandler {

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof Player player) {
            CompoundTag tag = getModTag(player, GuideMod.ID);
            if (GuideConfig.COMMON.canSpawnWithBook.get()) {
                for (Book book : GuideAPI.getBooks().values()) {
                    ModConfigSpec.BooleanValue bookSpawnConfig = GuideConfig.COMMON.SPAWN_BOOKS.get(book);
                    if ((bookSpawnConfig == null || bookSpawnConfig.get()) && !tag.getBoolean("hasInitial" + book.getRegistryName().toString()).orElse(false)) {
                        player.getInventory().add(new ItemStack(GuideAPI.getItemForBook(book)));
                        tag.putBoolean("hasInitial" + book.getRegistryName().toString(), true);
                    }
                }
            }
        }
    }


    public static CompoundTag getModTag(Player player, String modName) {
        CompoundTag tag = player.getPersistentData();
        CompoundTag persistTag = tag.getCompoundOrEmpty(Player.PERSISTED_NBT_TAG);
        CompoundTag modTag = persistTag.getCompoundOrEmpty(modName);
        persistTag.put(modName, modTag);
        tag.put(Player.PERSISTED_NBT_TAG, persistTag);
        return modTag;
    }
}
