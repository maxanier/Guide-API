package de.maxanier.guideapi.api.book;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

import javax.annotation.Nonnull;

/**
 * Base class for all {@link Book} related events.
 * <p>
 * {@link #book} is the book being opened.
 * {@link #player} is the player opening the book.
 */
public class BookEvent extends Event {

    private final Book book;
    private final Player player;

    protected BookEvent(Book book, Player player) {
        this.book = book;
        this.player = player;
    }

    public Book getBook() {
        return book;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Called client side whenever a book is opened.
     * <p>
     * {@link #canceledText} is a status message sent to the player when the book fails to open.
     */
    public static class Open extends BookEvent implements ICancellableEvent {

        private static final Component DEFAULT_CANCEL = Component.translatable("text.open.failed").withStyle(ChatFormatting.RED);

        private Component canceledText = DEFAULT_CANCEL;

        public Open(Book book, Player player) {
            super(book, player);
        }

        @Nonnull
        public Component getCanceledText() {
            return canceledText;
        }

        public void setCanceledText(@Nonnull Component canceledText) {
            this.canceledText = canceledText;
        }
    }
}
