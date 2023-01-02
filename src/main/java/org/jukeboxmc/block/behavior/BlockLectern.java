package org.jukeboxmc.block.behavior;

import com.nukkitx.nbt.NbtMap;
import org.jukeboxmc.block.Block;
import org.jukeboxmc.block.direction.BlockFace;
import org.jukeboxmc.block.direction.Direction;
import org.jukeboxmc.blockentity.BlockEntity;
import org.jukeboxmc.blockentity.BlockEntityLectern;
import org.jukeboxmc.blockentity.BlockEntityType;
import org.jukeboxmc.item.Item;
import org.jukeboxmc.math.Vector;
import org.jukeboxmc.player.Player;
import org.jukeboxmc.util.Identifier;
import org.jukeboxmc.world.World;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class BlockLectern extends Block {

    public BlockLectern( Identifier identifier ) {
        super( identifier );
    }

    public BlockLectern( Identifier identifier, NbtMap blockStates ) {
        super( identifier, blockStates );
    }

    @Override
    public boolean placeBlock( Player player, World world, Vector blockPosition, Vector placePosition, Vector clickedPosition, Item itemInHand, BlockFace blockFace ) {
        this.setDirection( player.getDirection().opposite() );

        boolean value = super.placeBlock( player, world, blockPosition, placePosition, clickedPosition, itemInHand, blockFace );
        if ( value ) {
            BlockEntity.create( BlockEntityType.LECTERN, this ).spawn();
        }
        return value;
    }

    @Override
    public BlockEntityLectern getBlockEntity() {
        return (BlockEntityLectern) this.location.getWorld().getBlockEntity( this.location, this.location.getDimension() );
    }

    public void setPowered( boolean value ) {
        this.setState( "powered_bit", value ? (byte) 1 : (byte) 0 );
    }

    public boolean isPowered() {
        return this.stateExists( "powered_bit" ) && this.getByteState( "powered_bit" ) == 1;
    }

    public void setDirection( Direction direction ) {
        switch ( direction ) {
            case SOUTH -> this.setState( "direction", 0 );
            case WEST -> this.setState( "direction", 1 );
            case NORTH -> this.setState( "direction", 2 );
            case EAST -> this.setState( "direction", 3 );
        }
    }

    public Direction getDirection() {
        int value = this.stateExists( "direction" ) ? this.getIntState( "direction" ) : 0;
        return switch ( value ) {
            case 0 -> Direction.SOUTH;
            case 1 -> Direction.WEST;
            case 2 -> Direction.NORTH;
            default -> Direction.EAST;
        };
    }
}