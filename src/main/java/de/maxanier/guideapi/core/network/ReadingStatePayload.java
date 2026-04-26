package de.maxanier.guideapi.core.network;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.book.IGuideItem;
import de.maxanier.guideapi.core.item.ItemGuideBookDataComponents;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;
import java.util.Optional;

/**
 * When closing the GuideBook on client-side, this payload informs the server about where the book was last opened. That information is written to the itemstacks DataComponents.
 */
public record ReadingStatePayload(int page, Optional<Integer> category,
                                  Optional<Identifier> entry) implements CustomPacketPayload {
    public static final Type<ReadingStatePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(GuideMod.ID, "reading_state"));
    public static final StreamCodec<ByteBuf, ReadingStatePayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ReadingStatePayload::page, ByteBufCodecs.VAR_INT.apply(ByteBufCodecs::optional), ReadingStatePayload::category, Identifier.STREAM_CODEC.apply(ByteBufCodecs::optional), ReadingStatePayload::entry, ReadingStatePayload::new);

    public static void handle(ReadingStatePayload msg, IPayloadContext context) {
        Player player = context.player();
        Objects.requireNonNull(player);

        ItemStack book = player.getOffhandItem();
        if (book.isEmpty() || !(book.getItem() instanceof IGuideItem))
            book = player.getMainHandItem();

        if (!book.isEmpty() && book.getItem() instanceof IGuideItem) {
            ItemStack finalBook = book;
            finalBook.set(ItemGuideBookDataComponents.PAGE, msg.page);
            msg.category.ifPresentOrElse(c -> finalBook.set(ItemGuideBookDataComponents.CATEGORY, c), () -> finalBook.remove(ItemGuideBookDataComponents.CATEGORY));
            msg.entry.ifPresentOrElse(e -> finalBook.set(ItemGuideBookDataComponents.ENTRY, e), () -> finalBook.remove(ItemGuideBookDataComponents.ENTRY));
        }

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
