package de.maxanier.guideapi.util;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public class BlockIdentifier {
    private final Holder<Block>[] blockHolders;
    private final TagKey<Block> blockTag;

    public BlockIdentifier(Block... blocks) {
        blockHolders = Stream.of(blocks).map(Holder::direct).toArray(Holder[]::new);
        blockTag = null;
    }

    public BlockIdentifier(Holder<Block> holder) {
        blockHolders = new Holder[]{holder};
        blockTag = null;
    }

    public BlockIdentifier(TagKey<Block> blockTag) {
        this.blockTag = blockTag;
        blockHolders = null;
    }

    public boolean matches(Block block) {
        if (blockTag != null) {
            return block.builtInRegistryHolder().is(blockTag);
        }
        if (blockHolders != null) {
            for (Holder<Block> blockHolder : blockHolders) {
                if (block == blockHolder.value()) {
                    return true;
                }
            }
        }
        return false;
    }
}
