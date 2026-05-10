package de.maxanier.guideapi.core.client;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.book.IGuideItem;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.world.IInfoOverlay;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
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
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

@EventBusSubscriber(value = Dist.CLIENT, modid = GuideMod.ID)
public class InfoOverlayRenderHandler {

    @Nullable
    private static Block cachedBlock = null;
    private static IInfoOverlay cachedBlockInfoRenderer = null;
    private static Book cachedBook = null;

    @SubscribeEvent
    public static void renderOverlay(RenderGuiLayerEvent.Pre event) {
        if (event.getName() != VanillaGuiLayers.CROSSHAIR)
            return;
        if (!renderBlockInfo(event.getGuiGraphics())) {
            cachedBlock = null;
            cachedBlockInfoRenderer = null;
            cachedBook = null;
        }
    }

    /**
     *
     * @return Whether the cache should be kept
     */
    private static boolean renderBlockInfo(GuiGraphicsExtractor graphics) {
        HitResult rayTrace = Minecraft.getInstance().hitResult;
        if (rayTrace == null || rayTrace.getType() != HitResult.Type.BLOCK)
            return false;

        Player player = Minecraft.getInstance().player;
        Level world = Minecraft.getInstance().level;
        if (player == null || world == null) {
            return false;
        }
        ItemStack held = ItemStack.EMPTY;
        Book book = null;
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack heldStack = player.getItemInHand(hand);
            if (heldStack.getItem() instanceof IGuideItem) {
                held = heldStack;
                book = ((IGuideItem) heldStack.getItem()).getBook(heldStack);
                break;
            }
        }

        if (book == null)
            return false;
        BlockPos rayTracePos = ((BlockHitResult) rayTrace).getBlockPos();
        BlockState state = world.getBlockState(rayTracePos);
        Block block = state.getBlock();
        ResourceKey<Block> blockKey = state.typeHolder().getKey();
        if (blockKey != null) {
            ItemStack finalHeld = held;
            book.getLinkedEntryForBlock(blockKey.identifier()).flatMap(book::findEntry).map(Pair::getRight).map(EntryBase::getName).ifPresent(entry -> {
                Font fontRenderer = Minecraft.getInstance().font;

                int drawX = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 10;
                int drawY = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 - 8;

                graphics.fakeItem(finalHeld, drawX, drawY);

                drawY -= 2;
                drawX += 20;
                graphics.text(fontRenderer, entry instanceof MutableComponent ? ((MutableComponent) entry).withStyle(ChatFormatting.WHITE) : entry, drawX, drawY, 0xFFFFFFFF, true);
                graphics.text(fontRenderer, Component.translatable("guideapi.text.linked.open").withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC), drawX, drawY + 12, 0xFFFFFFFF, true);
            });
        }


        if (block instanceof IInfoOverlay.Block) {
            IInfoOverlay infoRenderer = ((IInfoOverlay.Block) block).getInfoOverlay(book, world, rayTracePos, state, rayTrace, player);
            if (infoRenderer != null)
                infoRenderer.drawInformation(graphics, book, world, rayTracePos, state, rayTrace, player);
        } else {
            if (block != cachedBlock || book != cachedBook) {
                cachedBlockInfoRenderer = GuideAPI.getInfoOverlay(book, block);
                cachedBlock = block;
                cachedBook = book;
            }
            if (cachedBlockInfoRenderer != null) {
                cachedBlockInfoRenderer.drawInformation(graphics, book, world, rayTracePos, state, rayTrace, player);
            }
            return true;
        }

        return false;
    }


}
