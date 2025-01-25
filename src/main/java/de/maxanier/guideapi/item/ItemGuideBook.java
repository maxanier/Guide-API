package de.maxanier.guideapi.item;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.BookEvent;
import de.maxanier.guideapi.api.IGuideItem;
import de.maxanier.guideapi.api.IGuideLinked;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
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


        BookEvent.Open event = new BookEvent.Open(book, heldStack, player);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            player.displayClientMessage(event.getCanceledText(), true);
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

        ItemStack stack = context.getItemInHand();
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());

        if (state.getBlock() instanceof IGuideLinked guideLinked) {
            Identifier entryKey = guideLinked.getLinkedEntry(context.getLevel(), context.getClickedPos(), context.getPlayer(), stack);
            if (entryKey == null)
                return InteractionResult.FAIL;

            for (CategoryAbstract category : book.getCategoryList()) {
                if (category.entries.containsKey(entryKey)) {
                    GuideMod.PROXY.openEntry(book, category, category.entries.get(entryKey), context.getPlayer(), stack);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    // IGuideItem

    /**
     * Set a custom translation key
     */
    protected void setTranslation_key(String name) {
        this.translation_key = Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(GuideMod.ID, name));
    }
}
