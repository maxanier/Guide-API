package de.maxanier.guideapi.api.pages;

import com.google.common.collect.Lists;
import de.maxanier.guideapi.LogHelper;
import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.util.BookHelper;
import de.maxanier.guideapi.api.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;

import java.net.URI;
import java.util.List;


public class PageHolderWithLinks implements IPage {

    private static final int X_OFFSET = 17;
    private static final int ENTRY_HEIGHT = 20;
    /**
     * Set via {@link de.maxanier.guideapi.core.APISetter#setScreenFactories()}
     */
    private static LinkedScreenFactory createLinkedScreen;
    private final IPage page;
    private final BookHelper bookHelper;
    private final List<Identifier> lateLinks = Lists.newArrayList();
    private final List<Link> links = Lists.newArrayList();
    private long lastLinkClick = 0;
    public PageHolderWithLinks(BookHelper bookHelper, IPage page) {
        this.page = page;
        this.bookHelper = bookHelper;
    }

    /**
     * Add a link
     *
     * @return This
     */
    public PageHolderWithLinks addLink(EntryBase entry) {
        links.add(new EntryLink(entry));
        return this;
    }

    /**
     * Add a resource location of an entry to be linked
     *
     * @return This
     */
    public PageHolderWithLinks addLink(Identifier entry) {
        lateLinks.add(entry);
        return this;
    }

    /**
     * Add a URL link
     *
     * @return This
     */
    public PageHolderWithLinks addLink(URLLink link) {
        links.add(link);
        return this;
    }

    @Override
    public boolean canSee(Book book, CategoryBase category, EntryBase entry, Player player) {
        return page.canSee(book, category, entry, player);
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        page.draw(graphics, book, category, entry, pageLeft, pageTop, mouseX, mouseY, screen, fontRendererObj);
    }

    @Override
    public void drawExtras(GuiGraphicsExtractor graphics, Book book, CategoryBase category, EntryBase entry, int pageLeft, int pageTop, int mouseX, int mouseY, GuideBookScreen screen, Font fontRendererObj) {
        int ll = pageLeft + screen.pageWidth() + X_OFFSET;
        int y = pageTop;
        for (Link l : links) {
            Component t = l.getDisplayName();
            graphics.text(fontRendererObj, t, ll, y, -1, true);
            if (l.width == 0) {
                l.width = fontRendererObj.width(t);
            }
            y += ENTRY_HEIGHT;
        }
        page.drawExtras(graphics, book, category, entry, pageLeft, pageTop, mouseX, mouseY, screen, fontRendererObj);
    }

    @Override
    public void onInit(RegistryAccess registryAccess, Book book, CategoryBase category, EntryBase entry, Player player) {
        while (!lateLinks.isEmpty()) {
            Identifier s = lateLinks.remove(0);
            EntryBase e = bookHelper.getLinkedEntry(s);
            if (e == null) {
                LogHelper.info("Failed to find linked entry " + s);
            } else {
                addLink(e);
            }
        }
        page.onInit(registryAccess, book, category, entry, player);
    }

    @Override
    public void onLeftClicked(Book book, CategoryBase category, EntryBase entry, double mouseX, double mouseY, Player player, GuideBookScreen screen) {
        if (mouseX > screen.pageLeft() + screen.pageWidth()) {
            //Avoid double/triple execution per click
            long lastClock = System.currentTimeMillis() / 4;
            if (lastClock != lastLinkClick) {
                lastLinkClick = lastClock;
                for (int i = 0; i < links.size(); i++) {
                    if (GuiHelper.isMouseBetween(mouseX, mouseY, screen.pageLeft() + screen.pageWidth() + X_OFFSET, screen.pageTop() + ENTRY_HEIGHT * i, links.get(i).width, 20)) {
                        links.get(i).onClicked(book, category, entry, player, screen.currentPage());
                        return;
                    }
                }
            }

        }
        page.onLeftClicked(book, category, entry, mouseX, mouseY, player, screen);
    }

    @Override
    public void onRightClicked(Book book, CategoryBase category, EntryBase entry, double mouseX, double mouseY, Player player, GuideBookScreen screen) {
        page.onRightClicked(book, category, entry, mouseX, mouseY, player, screen);
    }

    @FunctionalInterface
    public interface LinkedScreenFactory {
        Screen create(Book book, CategoryBase category, EntryBase entry, Player player, EntryBase from, int fromPage);
    }

    private static abstract class Link {
        public int width;

        public abstract Component getDisplayName();

        public abstract void onClicked(Book book, CategoryBase category, EntryBase entry, Player player, int page);
    }

    public static class URLLink extends Link {
        private final Component name;
        private final URI link;

        public URLLink(String name, URI link) {
            this.name = Component.literal(name);
            this.link = link;
        }

        public URLLink(Component name, URI link) {
            this.name = name;
            this.link = link;
        }

        @Override
        public Component getDisplayName() {
            return name;
        }

        @Override
        public void onClicked(Book book, CategoryBase category, EntryBase entry, Player player, int page) {
            Util.getPlatform().openUri(link);
        }
    }

    private static class EntryLink extends Link {
        private final EntryBase linkedEntry;

        private EntryLink(EntryBase entry) {
            this.linkedEntry = entry;
        }

        @Override
        public Component getDisplayName() {
            return linkedEntry.getName();
        }

        @Override
        public void onClicked(Book book, CategoryBase category, EntryBase entry, Player player, int page) {
            openLinkedEntry(book, category, linkedEntry, player, entry, page);
        }

        /**
         * Simply opens a gui screen with a GuiLinkedEntry.
         */
        private void openLinkedEntry(Book book, CategoryBase category, EntryBase entry, Player player, EntryBase from, int fromPage) {
            Minecraft.getInstance().setScreen(createLinkedScreen.create(book, category, entry, player, from, fromPage));
        }
    }


}
