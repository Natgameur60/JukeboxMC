package org.jukeboxmc.api.block

import org.jukeboxmc.api.block.data.Direction

interface PoweredRepeater : Block {

   fun getCardinalDirection(): Direction

   fun setCardinalDirection(value: Direction): PoweredRepeater

   fun getRepeaterDelay(): Int

   fun setRepeaterDelay(value: Int): PoweredRepeater

}