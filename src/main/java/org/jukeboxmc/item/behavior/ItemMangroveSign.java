package org.jukeboxmc.item.behavior;

import org.jukeboxmc.block.Block;
import org.jukeboxmc.block.BlockType;
import org.jukeboxmc.item.Burnable;
import org.jukeboxmc.item.Item;
import org.jukeboxmc.item.ItemType;
import org.jukeboxmc.util.Identifier;

import java.time.Duration;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class ItemMangroveSign extends Item implements Burnable {

    public ItemMangroveSign( Identifier identifier ) {
        super( identifier );
    }

    public ItemMangroveSign( ItemType itemType ) {
        super( itemType );
    }

    @Override
    public Block toBlock() {
        return Block.create( BlockType.MANGROVE_STANDING_SIGN );
    }

    @Override
    public Duration getBurnTime() {
        return Duration.ofMillis( 200 );
    }
}