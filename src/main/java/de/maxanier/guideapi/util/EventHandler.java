package de.maxanier.guideapi.util;

import com.google.common.collect.Multimap;
import de.maxanier.guideapi.GuideConfig;
import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.IGuideItem;
import de.maxanier.guideapi.api.IGuideLinked;
import de.maxanier.guideapi.api.IInfoRenderer;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import javax.annotation.Nullable;
import java.util.Collection;

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
