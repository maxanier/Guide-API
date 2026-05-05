package de.maxanier.guideapi.api.util;

import de.maxanier.guideapi.api.pages.IPage;
import de.maxanier.guideapi.api.pages.PageItemStack;
import de.maxanier.guideapi.api.pages.PageText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class PageHelper {


    /**
     * Split the given text into multiple section if it does not fit one page.
     * The first page can have a different number of lines than the subsequent ones if desired
     * Insert new line characters to wrap the text to the available line width.
     *
     * @param text             Text component to process
     * @param lineWidth        Available width (pixel)
     * @param firstHeight      Available height on the first page (pixel)
     * @param subsequentHeight Available height on subsequent pages (pixel)
     * @return Each list element should be drawn on a individual page. Lines are wrapped using '\n'
     */
    public static List<FormattedText> prepareForLongText(FormattedText text, int lineWidth, int firstHeight, int subsequentHeight) {
        Font fontRenderer = Minecraft.getInstance().font;
        int firstCount = firstHeight / fontRenderer.lineHeight;
        int count = subsequentHeight / fontRenderer.lineHeight;
        List<FormattedText> lines = new ArrayList<>(fontRenderer.getSplitter().splitLines(text, lineWidth, Style.EMPTY));
        List<FormattedText> pages = new ArrayList<>();

        List<FormattedText> pageLines = lines.size() > firstCount ? lines.subList(0, firstCount) : lines;
        pages.add(combineWithNewLine(pageLines));
        pageLines.clear();
        while (!lines.isEmpty()) {
            pageLines = lines.size() > count ? lines.subList(0, count) : lines;
            pages.add(combineWithNewLine(pageLines));
            pageLines.clear();
        }
        return pages;
    }


    /**
     * Spread the text over multiple pages if necessary. Display ingredient at first page
     */
    public static List<IPage> pagesForLongText(FormattedText text, Ingredient ingredient) {
        List<FormattedText> pageText = prepareForLongText(text, 164, 79, 126);
        List<IPage> pageList = new ArrayList<>();
        for (int i = 0; i < pageText.size(); i++) {
            if (i == 0) {
                pageList.add(new PageItemStack(pageText.get(i), ingredient));
            } else {
                pageList.add(new PageText(pageText.get(i)));
            }
        }
        return pageList;
    }


    public static List<IPage> pagesForLongText(FormattedText text) {
        List<IPage> pageList = new ArrayList<>();
        prepareForLongText(text, 164, 126, 126).forEach(t -> pageList.add(new PageText(t)));
        return pageList;
    }


    public static void drawFormattedText(GuiGraphicsExtractor graphics, int x, int y, FormattedText toDraw, int color) {
        Font fontRenderer = Minecraft.getInstance().font;

        List<FormattedCharSequence> cutLines = fontRenderer.split(toDraw, 170);
        for (FormattedCharSequence cut : cutLines) {
            graphics.text(fontRenderer, cut, x, y, color, false);
            y += 10;
        }

    }

    /**
     * @param text - Text
     * @param item - The item to put on the first page
     * @return a list of IPages with the text cut to fit on page
     */
    public static List<IPage> pagesForLongText(FormattedText text, Item item) {
        return pagesForLongText(text, Ingredient.of(item));
    }

    /**
     * @param text  - Text
     * @param block - The block to put on the first page
     * @return a list of IPages with the text cut to fit on page
     */
    public static List<IPage> pagesForLongText(FormattedText text, Block block) {
        return pagesForLongText(text, Ingredient.of(block));
    }



    /**
     * @param elements The list ist not used itself, but the elements are passed to the new ITextProperties
     * @return a new ITextProperties that combines the given elements with a newline in between
     */
    private static FormattedText combineWithNewLine(List<FormattedText> elements) {
        FormattedText newLine = Component.literal("\n");
        List<FormattedText> copy = new ArrayList<>(elements.size() * 2);
        for (int i = 0; i < elements.size() - 1; i++) {
            copy.add(elements.get(i));
            copy.add(newLine);
        }
        copy.add(elements.getLast());
        return FormattedText.composite(copy);
    }
}
