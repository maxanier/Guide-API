package de.maxanier.guideapi.api.util;

import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.book.Book;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.stream.Stream;

/**
 * You can use this to generate the model for your guidebook during data generation.
 */
public class ModelHelper {

    public static void generateDefaultGuidebookModel(ItemModelGenerators generators, Book book) {
        Identifier baseTexture = Identifier.fromNamespaceAndPath(GuideMod.ID, "item/book_base");
        Identifier genericPageTexture = Identifier.fromNamespaceAndPath(GuideMod.ID, "item/book_page");
        Item item = GuideAPI.getItemForBook(book).value();
        Identifier model = ModelTemplates.TWO_LAYERED_ITEM.create(ModelLocationUtils.decorateItemModelLocation(item.toString()), TextureMapping.layered(baseTexture, genericPageTexture), generators.modelOutput);
        generators.itemModelOutput.accept(item, ItemModelUtils.tintedModel(model, ItemModelUtils.constantTint(book.getThemeColor())));


    }

    public static Stream<? extends Holder<Item>> getBookItems(Book... books) {
        return Stream.of(books).map(GuideAPI::getItemForBook);
    }
}
