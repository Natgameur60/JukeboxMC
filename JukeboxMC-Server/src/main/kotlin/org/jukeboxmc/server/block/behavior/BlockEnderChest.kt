package org.jukeboxmc.server.block.behavior

import org.cloudburstmc.nbt.NbtMap
import org.jukeboxmc.api.Identifier
import org.jukeboxmc.api.block.EnderChest
import org.jukeboxmc.api.block.data.BlockFace
import org.jukeboxmc.api.block.data.Direction
import org.jukeboxmc.api.blockentity.BlockEntity
import org.jukeboxmc.api.blockentity.BlockEntityType
import org.jukeboxmc.api.math.Vector
import org.jukeboxmc.server.block.JukeboxBlock
import org.jukeboxmc.server.extensions.toJukeboxBlockEntity
import org.jukeboxmc.server.item.JukeboxItem
import org.jukeboxmc.server.player.JukeboxPlayer
import org.jukeboxmc.server.world.JukeboxWorld

class BlockEnderChest(identifier: Identifier, blockStates: NbtMap?) : JukeboxBlock(identifier, blockStates),
    EnderChest {

    override fun placeBlock(
        player: JukeboxPlayer,
        world: JukeboxWorld,
        blockPosition: Vector,
        placePosition: Vector,
        clickedPosition: Vector,
        itemInHand: JukeboxItem,
        blockFace: BlockFace
    ): Boolean {
        val block = world.getBlock(placePosition)
        if (block is BlockWater && block.getLiquidDepth() == 0) {
            world.setBlock(placePosition, block, 1, false)
        }
        this.setCardinalDirection(player.getDirection().opposite())
        BlockEntity.create(BlockEntityType.ENDERCHEST, this)?.spawn()
        return super.placeBlock(player, world, blockPosition, placePosition, clickedPosition, itemInHand, blockFace)
    }

    override fun interact(
        player: JukeboxPlayer,
        world: JukeboxWorld,
        blockPosition: Vector,
        clickedPosition: Vector,
        blockFace: BlockFace,
        itemInHand: JukeboxItem
    ): Boolean {
        world.getBlockEntity(this.getLocation())?.toJukeboxBlockEntity()
            ?.interact(player, blockPosition, clickedPosition, blockFace, itemInHand)
        return true
    }

    override fun getCardinalDirection(): Direction {
        return Direction.valueOf(this.getStringState("minecraft:cardinal_direction"))
    }

    override fun setCardinalDirection(value: Direction): BlockEnderChest {
        return this.setState("minecraft:cardinal_direction", value.name.lowercase())
    }

    override fun getWaterLoggingLevel(): Int {
        return 1
    }
}