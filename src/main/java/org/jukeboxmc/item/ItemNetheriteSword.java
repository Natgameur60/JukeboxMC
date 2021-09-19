package org.jukeboxmc.item;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class ItemNetheriteSword extends Item implements Durability {

    public ItemNetheriteSword() {
        super ( "minecraft:netherite_sword" );
    }

    @Override
    public ItemToolType getItemToolType() {
        return ItemToolType.SWORD;
    }

    @Override
    public ItemTierType getTierType() {
        return ItemTierType.NETHERITE;
    }

    @Override
    public int getMaxDurability() {
        return 2031;
    }
}
