package org.jukeboxmc.item.behavior;

import org.jukeboxmc.item.ItemType;
import org.jukeboxmc.util.Identifier;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class ItemTropicalFish extends ItemFood {

    public ItemTropicalFish( Identifier identifier ) {
        super( identifier );
    }

    public ItemTropicalFish( ItemType itemType ) {
        super( itemType );
    }

    @Override
    public float getSaturation() {
        return 0.2f;
    }

    @Override
    public int getHunger() {
        return 1;
    }
}