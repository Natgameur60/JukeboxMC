package org.jukeboxmc.item.behavior;

import org.jukeboxmc.item.ItemType;
import org.jukeboxmc.util.Identifier;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class ItemRabbit extends ItemFood {

    public ItemRabbit( Identifier identifier ) {
        super( identifier );
    }

    public ItemRabbit( ItemType itemType ) {
        super( itemType );
    }

    @Override
    public float getSaturation() {
        return 1.8f;
    }

    @Override
    public int getHunger() {
        return 3;
    }
}