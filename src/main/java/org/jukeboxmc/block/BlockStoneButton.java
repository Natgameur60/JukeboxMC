package org.jukeboxmc.block;

import org.jukeboxmc.item.ItemStoneButton;
import org.jukeboxmc.item.ItemToolType;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class BlockStoneButton extends BlockButton {

    public BlockStoneButton() {
        super( "minecraft:stone_button" );
    }

    @Override
    public ItemStoneButton toItem() {
        return new ItemStoneButton();
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.STONE_BUTTON;
    }

    @Override
    public ItemToolType getToolType() {
        return ItemToolType.PICKAXE;
    }
}