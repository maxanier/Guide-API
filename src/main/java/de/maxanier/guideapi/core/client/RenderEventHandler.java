package de.maxanier.guideapi.core.client;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.book.IGuideItem;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.world.IGuideLinked;
import de.maxanier.guideapi.api.world.IInfoRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
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

import javax.annotation.Nullable;

@EventBusSubscriber(value = Dist.CLIENT, modid = GuideMod.ID)
public class RenderEventHandler {

    @Nullable
    private static Block cachedBlock = null;
    private static IInfoRenderer cachedBlockInfoRenderer = null;

    @SubscribeEvent
    public static void renderOverlay(RenderGuiLayerEvent.Pre event) {
        if (event.getName() != VanillaGuiLayers.CROSSHAIR)
            return;
        if (!renderBlockInfo(event.getGuiGraphics())) {
            cachedBlock = null;
            cachedBlockInfoRenderer = null;
        }
    }

    /**
     * <
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

        @Nullable
        Component linkedEntry = null;
        if (block instanceof IGuideLinked linked) {
            Identifier entryKey = linked.getLinkedEntry(world, rayTracePos, player, held);
            if (entryKey != null) {
                for (CategoryBase category : book.getCategoryList()) {
                    if (category.entries.containsKey(entryKey)) {
                        linkedEntry = category.getEntry(entryKey).getName();
                        break;
                    }
                }
            }
        }


        if (linkedEntry != null) {
            Font fontRenderer = Minecraft.getInstance().font;

            int drawX = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 10;
            int drawY = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 - 8;

            graphics.fakeItem(held, drawX, drawY);

            drawY -= 2;
            drawX += 20;
            graphics.text(fontRenderer, linkedEntry instanceof MutableComponent ? ((MutableComponent) linkedEntry).withStyle(ChatFormatting.WHITE) : linkedEntry, drawX, drawY, 0xFFFFFFFF, true);
            graphics.text(fontRenderer, Component.translatable("guideapi.text.linked.open").withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC), drawX, drawY + 12, 0xFFFFFFFF, true);
        }

        if (block instanceof IInfoRenderer.Block) {
            IInfoRenderer infoRenderer = ((IInfoRenderer.Block) block).getInfoRenderer(book, world, rayTracePos, state, rayTrace, player);
            if (infoRenderer != null)
                infoRenderer.drawInformation(graphics, book, world, rayTracePos, state, rayTrace, player);
        } else {
            if (block != cachedBlock) {
                cachedBlockInfoRenderer = GuideAPI.getInfoRendererForBlock(book, block);
                cachedBlock = block;
            }
            if (cachedBlockInfoRenderer != null) {
                cachedBlockInfoRenderer.drawInformation(graphics, book, world, rayTracePos, state, rayTrace, player);
            }
            return true;
        }

        return false;
    }


}
