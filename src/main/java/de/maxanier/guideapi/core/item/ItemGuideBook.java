package de.maxanier.guideapi.core.item;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.book.BookEvent;
import de.maxanier.guideapi.api.book.IGuideItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ItemGuideBook extends Item implements IGuideItem {

    @Nonnull
    private final Book book;
    private String translation_key;


    public ItemGuideBook(Book book, Item.Properties properties) {
        super(properties.stacksTo(1));
        this.book = book;
        setTranslation_key(GuideMod.ID + ".book." + book.getRegistryName().getNamespace() + "." + book.getRegistryName().getPath());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        if (book.getAuthor() != null) {
            tooltipAdder.accept(book.getAuthor());
            if (flag == Default.ADVANCED) {
                tooltipAdder.accept(Component.literal(book.getRegistryName().toString()));
            }
        }
    }


    @Override
    public Book getBook(ItemStack stack) {
        return book;
    }

    @Nullable
    @Override
    public String getCreatorModId(HolderLookup.Provider registries, ItemStack itemStack) {
        return book.getRegistryName().getNamespace();
    }

    @Nonnull
    @Override
    public Component getName(ItemStack stack) {
        return getBook(stack).getItemName() != null ? getBook(stack).getItemName() : super.getName(stack);
    }

    @Nonnull
    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {

        ItemStack heldStack = player.getItemInHand(hand);

        //Only handle book client side
        if (!world.isClientSide()) return InteractionResult.SUCCESS;


        BookEvent.Open event = new BookEvent.Open(book, player);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            player.sendOverlayMessage(event.getCanceledText());
            return InteractionResult.FAIL;
        }
        GuideMod.PROXY.openGuidebook(player, world, book, heldStack);
        return InteractionResult.SUCCESS_SERVER;
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide() || !context.isSecondaryUseActive())
            return InteractionResult.PASS;

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        BlockState state = context.getLevel().getBlockState(context.getClickedPos());

        ResourceKey<Block> blockKey = state.typeHolder().getKey();
        if (blockKey != null) {
            return book.getLinkedEntryForBlock(blockKey.identifier()).flatMap(book::findEntry).map(entry -> {
                BookEvent.Open event = new BookEvent.Open(book, player);
                NeoForge.EVENT_BUS.post(event);
                if (event.isCanceled()) {
                    player.sendOverlayMessage(event.getCanceledText());
                    return InteractionResult.FAIL;
                }
                GuideMod.PROXY.openEntry(book, entry.getLeft(), entry.getRight(), player);
                return InteractionResult.SUCCESS;
            }).orElse(InteractionResult.PASS);
        }

        return InteractionResult.PASS;
    }

    /**
     * Set a custom translation key
     */
    protected void setTranslation_key(String name) {
        this.translation_key = Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(GuideMod.ID, name));
    }
}
