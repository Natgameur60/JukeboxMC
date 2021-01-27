package org.jukeboxmc.block;

import org.jukeboxmc.block.direction.BlockFace;
import org.jukeboxmc.item.Item;
import org.jukeboxmc.math.BlockPosition;
import org.jukeboxmc.math.Vector;
import org.jukeboxmc.player.Player;
import org.jukeboxmc.world.World;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class BlockDoubleEndStoneBrickSlab extends BlockSlab {

    public BlockDoubleEndStoneBrickSlab() {
        super( "minecraft:double_stone_slab3" );
    }

    @Override
    public void placeBlock( Player player, World world, BlockPosition placePosition, Vector clickedPosition, Item itemIndHand, BlockFace blockFace ) {
        super.placeBlock( player, world, placePosition, clickedPosition, itemIndHand, blockFace );
        this.setStoneSlabType( StoneSlabType.values()[itemIndHand.getMeta()] );
        world.setBlock( placePosition, this );
    }

    public void setStoneSlabType( StoneSlabType stoneSlabType ) {
        this.setState( "stone_slab_type_4", stoneSlabType.name().toLowerCase() );
    }

    public StoneSlabType getStoneSlabType() {
        return this.stateExists( "stone_slab_type_4" ) ? StoneSlabType.valueOf( this.getStringState( "stone_slab_type_4" ).toUpperCase() ) : StoneSlabType.STONE;
    }

    public enum StoneSlabType {
        MOSSY_STONE_BRICK,
        SMOOTH_QUARTZ,
        STONE,
        CUT_SANDSTONE,
        CUT_RED_SANDSTONE
    }

}
