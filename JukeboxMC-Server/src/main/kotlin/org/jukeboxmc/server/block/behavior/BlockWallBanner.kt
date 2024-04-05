package org.jukeboxmc.server.block.behavior

import org.cloudburstmc.nbt.NbtMap
import org.jukeboxmc.api.Identifier
import org.jukeboxmc.api.block.WallBanner
import org.jukeboxmc.api.block.data.BlockFace
import org.jukeboxmc.server.block.JukeboxBlock

class BlockWallBanner(identifier: Identifier, blockStates: NbtMap?) : JukeboxBlock(identifier, blockStates),
    WallBanner {

    override fun canPassThrough(): Boolean {
        return true
    }

    override fun getFacingDirection(): BlockFace {
        return BlockFace.entries[this.getIntState("facing_direction")]
    }

    override fun setFacingDirection(value: BlockFace): WallBanner {
        return this.setState("facing_direction", value.ordinal)
    }
}