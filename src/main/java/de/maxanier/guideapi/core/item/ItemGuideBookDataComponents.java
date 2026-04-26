package de.maxanier.guideapi.core.item;

import de.maxanier.guideapi.GuideMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Holds Data Component Types used for ItemGuideBook
 */
public class ItemGuideBookDataComponents {

    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, GuideMod.ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PAGE = DATA_COMPONENTS.registerComponentType("page", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CATEGORY = DATA_COMPONENTS.registerComponentType("category", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Identifier>> ENTRY = DATA_COMPONENTS.registerComponentType("entry", builder -> builder.persistent(Identifier.CODEC).networkSynchronized(Identifier.STREAM_CODEC));

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}
