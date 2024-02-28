package org.jukeboxmc.server.recipe

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.ContainerMixData
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.CraftingDataType
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.PotionMixData
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.NetworkRecipeData
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.RecipeData
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.ShapedRecipeData
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.ShapelessRecipeData
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ComplexAliasDescriptor
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ItemDescriptorWithCount
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ItemTagDescriptor
import org.jukeboxmc.api.Server
import org.jukeboxmc.api.extensions.fromJson
import org.jukeboxmc.server.JukeboxServer
import org.jukeboxmc.server.block.RuntimeBlockDefinition
import org.jukeboxmc.server.item.JukeboxItem
import org.jukeboxmc.server.util.ItemPalette
import org.jukeboxmc.server.util.PaletteUtil
import java.util.*


class RecipeManager {

    private val craftingData: MutableList<RecipeData> = mutableListOf()
    private val containerMixData: MutableList<ContainerMixData> = mutableListOf()
    private val potionMixData: MutableList<PotionMixData> = mutableListOf()

    init {
        val stream = ItemPalette::class.java.classLoader.getResourceAsStream("recipes.json")
            ?: throw RuntimeException("The item palette was not found")
        val gson = Gson()

        stream.reader().use {
            val fromJosn = gson.fromJson<JsonObject>(it)
            val recipes = fromJosn.getAsJsonArray("recipes")
            for (element in recipes) {
                val jsonObject = element.asJsonObject
                val type = CraftingDataType.entries[jsonObject["type"].asInt]

                if (type == CraftingDataType.SHAPELESS && type == CraftingDataType.SHULKER_BOX) {
                    val id = jsonObject["id"].asString
                    val block = jsonObject["block"].asString
                    val uuid = UUID.fromString(jsonObject["uuid"].asString)
                    val netId = jsonObject["netId"].asInt
                    val priority = jsonObject["priority"].asInt

                    val inputItems: MutableList<ItemDescriptorWithCount> = mutableListOf()
                    for (inputElement in jsonObject["input"].asJsonArray) {
                        val inputObject = inputElement.asJsonObject

                        val count = inputObject["count"].asInt
                        val identifier = inputObject["itemId"].asString
                        val auxValue = inputObject["auxValue"].asInt
                        val runtimeId = ItemPalette.getRuntimeId(identifier)
                        inputItems.add(
                            ItemDescriptorWithCount.fromItem(
                                ItemData.builder()
                                    .definition(SimpleItemDefinition(identifier, runtimeId, false))
                                    .damage(auxValue)
                                    .count(count)
                                    .build()
                            )
                        )
                    }
                    val outputItems: MutableList<ItemData> = mutableListOf()
                    for (outputElement in jsonObject["output"].asJsonArray) {
                        val outputObject = outputElement.asJsonObject
                        val identifier = outputObject["id"].asString
                        val count = outputObject["count"].asInt
                        val runtimeId = ItemPalette.getRuntimeId(identifier)
                        outputItems.add(
                            ItemData.builder()
                                .definition(SimpleItemDefinition(identifier, runtimeId, false))
                                .blockDefinition(RuntimeBlockDefinition(PaletteUtil.identifierToBlockRuntimeId[identifier]!!))
                                .count(count)
                                .build()
                        )
                    }
                    /*
                    this.craftingData.add(
                        ShapelessRecipeData.shapeless(
                            id,
                            inputItems,
                            outputItems,
                            uuid,
                            block,
                            priority,
                            netId
                        )
                    )
                     */
                } else if (type == CraftingDataType.SHAPED) {
                    val identifier = jsonObject["id"].asString
                    val block = jsonObject["block"].asString
                    val uuid = UUID.fromString(jsonObject["uuid"].asString)
                    val netId = jsonObject["netId"].asInt
                    val priority = jsonObject["priority"].asInt
                    val width = jsonObject["width"].asInt
                    val height = jsonObject["height"].asInt

                    val inputItems: MutableList<ItemDescriptorWithCount> = mutableListOf()
                    val charMap: MutableMap<Char, ItemDescriptorWithCount> = mutableMapOf()


                    for (entry in jsonObject["input"].asJsonObject.entrySet()) {
                        val entryJsonObject = entry.value.asJsonObject
                        val itemType = entryJsonObject["type"].asString

                        if (itemType.equals("default")) {
                            if (!charMap.containsKey(entry.key[0])) {
                                val id = entryJsonObject["itemId"].asString
                                val runtimeId = ItemPalette.getRuntimeId(id)
                                charMap[entry.key[0]] = ItemDescriptorWithCount.fromItem(
                                    ItemData.builder()
                                        .definition(SimpleItemDefinition(id, runtimeId, false))
                                        .damage(entryJsonObject["auxValue"].asInt)
                                        .count(entryJsonObject["count"].asInt)
                                        .build()
                                )
                            }
                        } else if (itemType.equals("item_tag")) {
                            if (!charMap.containsKey(entry.key[0])) {
                                charMap[entry.key[0]] = ItemDescriptorWithCount(
                                    ItemTagDescriptor(entryJsonObject["itemTag"].asString),
                                    1
                                )
                            }
                        } else if (itemType.equals("complex_alias")) {
                            if (!charMap.containsKey(entry.key[0])) {
                                charMap[entry.key[0]] = ItemDescriptorWithCount(
                                    ComplexAliasDescriptor(entryJsonObject["name"].asString),
                                    1
                                )
                            }
                        }
                    }

                    //Thread.sleep(1000* 5)

                    for (y in 0 until height) {
                        for (x in 0 until width) {
                            val value = jsonObject["shape"].asJsonArray[y].asString[x]
                            if (charMap.containsKey(value)){
                                inputItems.add(charMap[value]!!)
                            }else{
                                inputItems.add(ItemDescriptorWithCount.fromItem(ItemData.AIR))
                            }
                        }
                    }

                    val outputItems: MutableList<ItemData> = mutableListOf()
                    for (outputElement in jsonObject["output"].asJsonArray) {
                        val outputObject = outputElement.asJsonObject
                        val id = outputObject["id"].asString
                        val count = outputObject["count"].asInt
                        val runtimeId = ItemPalette.getRuntimeId(id)
                        outputItems.add(
                            ItemData.builder()
                                .definition(SimpleItemDefinition(id, runtimeId, false))
                                .blockDefinition(RuntimeBlockDefinition(PaletteUtil.identifierToBlockRuntimeId[id]!!))
                                .count(count)
                                .build()
                        )
                    }
                    val shaped = ShapedRecipeData.shaped(
                        identifier,
                        width,
                        height,
                        inputItems,
                        outputItems,
                        uuid,
                        block,
                        priority,
                        netId
                    )
                    //this.craftingData.add(shaped)
                } else if (type == CraftingDataType.SMITHING_TRANSFORM) {
                    val id = jsonObject["id"].asString
                    val block = jsonObject["block"].asString
                    val netId = jsonObject["netId"].asInt

                    val baseObject = jsonObject["base"].asJsonObject
                    val baseItemId = baseObject["itemId"].asString
                    val baseRuntimeId = ItemPalette.getRuntimeId(baseItemId)
                    val baseDescriptor: ItemDescriptorWithCount = ItemDescriptorWithCount.fromItem(
                        ItemData.builder()
                            .definition(SimpleItemDefinition(baseItemId, baseRuntimeId, false))
                            .damage(baseObject["auxValue"].asInt)
                            .count(baseObject["count"].asInt)
                            .build()
                    )

                    val additionObject = jsonObject["addition"].asJsonObject
                    val additionItemId = additionObject["itemId"].asString
                    val additionRuntimeId = ItemPalette.getRuntimeId(additionItemId)
                    val additionDescriptor: ItemDescriptorWithCount = ItemDescriptorWithCount.fromItem(
                        ItemData.builder()
                            .definition(SimpleItemDefinition(additionItemId, additionRuntimeId, false))
                            .damage(additionObject["auxValue"].asInt)
                            .count(additionObject["count"].asInt)
                            .build()
                    )

                    val templateObject = jsonObject["template"].asJsonObject
                    val templateItemId = templateObject["itemId"].asString
                    val templateRuntimeId = ItemPalette.getRuntimeId(templateItemId)
                    val templateDescriptor: ItemDescriptorWithCount = ItemDescriptorWithCount.fromItem(
                        ItemData.builder()
                            .definition(SimpleItemDefinition(templateItemId, templateRuntimeId, false))
                            .damage(templateObject["auxValue"].asInt)
                            .count(templateObject["count"].asInt)
                            .build()
                    )

                    val resultObject = jsonObject["result"].asJsonObject
                    val resultId = resultObject["id"].asString
                    val resultRuntimeId = ItemPalette.getRuntimeId(resultId)
                    val resultDescriptor: ItemData = ItemData.builder()
                        .definition(SimpleItemDefinition(resultId, resultRuntimeId, false))
                        .damage(0)
                        .count(resultObject["count"].asInt)
                        .build()

                    //this.craftingData.add(SmithingTransformRecipeData.of(id, baseDescriptor, additionDescriptor, templateDescriptor, resultDescriptor, block, netId))
                } else if (type == CraftingDataType.SMITHING_TRIM) {
                    val id = jsonObject["id"].asString
                    val block = jsonObject["block"].asString
                    val netId = jsonObject["netId"].asInt

                    val baseObject = jsonObject["base"].asJsonObject
                    val baseDescriptor = ItemDescriptorWithCount(ItemTagDescriptor(baseObject["itemTag"].asString), baseObject["count"].asInt)

                    val additionObject = jsonObject["addition"].asJsonObject
                    val additionDescriptor = ItemDescriptorWithCount(ItemTagDescriptor(additionObject["itemTag"].asString), additionObject["count"].asInt)

                    val templateObject = jsonObject["template"].asJsonObject
                    val templateDescriptor = ItemDescriptorWithCount(ItemTagDescriptor(templateObject["itemTag"].asString), templateObject["count"].asInt)

                    //this.craftingData.add(SmithingTrimRecipeData.of(id, baseDescriptor, additionDescriptor, templateDescriptor, block, netId))
                } else if (type == CraftingDataType.MULTI) {
                    //this.craftingData.add(MultiRecipeData.of(UUID.fromString(jsonObject["uuid"].asString), jsonObject["netId"].asInt))
                } else if (type == CraftingDataType.FURNACE_DATA) {
                    val block = jsonObject["block"].asString
                    val inputObject = jsonObject["input"].asJsonObject
                    val outputObject = jsonObject["output"].asJsonObject
                    /*
                    this.craftingData.add(FurnaceRecipeData.of(
                        ItemPalette.getRuntimeId(inputObject["id"].asString),
                        ItemData.builder()
                            .definition(SimpleItemDefinition(outputObject["id"].asString, ItemPalette.getRuntimeId(outputObject["id"].asString), false))
                            .damage(0)
                            .count(outputObject["count"].asInt)
                            .build(),
                        block))
                     */
                }
            }

            val containerMixes = fromJosn.getAsJsonArray("containerMixes")
            for (element in containerMixes) {
                val jsonObject = element.asJsonObject
                val inputId = ItemPalette.getRuntimeId(jsonObject["inputId"].asString)
                val reagentId = ItemPalette.getRuntimeId(jsonObject["reagentId"].asString)
                val outputId = ItemPalette.getRuntimeId(jsonObject["outputId"].asString)
                this.containerMixData.add(ContainerMixData(inputId, reagentId, outputId))
            }

            val potionMixes = fromJosn.getAsJsonArray("potionMixes")
            for (element in potionMixes) {
                val jsonObject = element.asJsonObject
                val inputId = ItemPalette.getRuntimeId(jsonObject["inputId"].asString)
                val inputMeta = jsonObject["inputMeta"].asInt
                val reagentId = ItemPalette.getRuntimeId(jsonObject["reagentId"].asString)
                val reagentMeta = jsonObject["reagentMeta"].asInt
                val outputId = ItemPalette.getRuntimeId(jsonObject["outputId"].asString)
                val outputMeta = jsonObject["outputMeta"].asInt
                this.potionMixData.add(PotionMixData(inputId, inputMeta, reagentId, reagentMeta, outputId, outputMeta))
            }
        }
    }

    fun registerRecipe(recipeId: String, recipe: Recipe) {
        try {
            craftingData.add(recipe.doRegister(this, recipeId))
        } catch (e: java.lang.RuntimeException) {
            JukeboxServer.getInstance().getLogger().error("Could not register recipe $recipeId!")
        }
    }

    fun getResultItem(recipeNetworkId: Int): List<JukeboxItem> {
        val collect = craftingData.stream()
            .filter { recipeData: RecipeData? -> recipeData is NetworkRecipeData }
            .map { recipeData: RecipeData -> recipeData as NetworkRecipeData }.toList()
        val optional = collect.stream()
            .filter { networkRecipeData: NetworkRecipeData -> networkRecipeData.netId == recipeNetworkId }
            .findFirst()
        if (optional.isPresent) {
            val networkRecipeData = optional.get()
            if (networkRecipeData is ShapedRecipeData) {
                val items: MutableList<JukeboxItem> = LinkedList()
                for (result in networkRecipeData.results) {
                    items.add(JukeboxItem(result, false))
                }
                return items
            } else if (networkRecipeData is ShapelessRecipeData) {
                val items: MutableList<JukeboxItem> = LinkedList()
                for (result in networkRecipeData.results) {
                    items.add(JukeboxItem(result, false))
                }
                return items
            }
        }
        return emptyList()
    }

    fun getHighestNetworkId(): Int {
        return craftingData.stream()
            .filter { recipeData: RecipeData? -> recipeData is NetworkRecipeData }
            .map { recipeData: RecipeData -> recipeData as NetworkRecipeData }
            .max(Comparator.comparing { obj: NetworkRecipeData -> obj.netId })
            .map { obj: NetworkRecipeData -> obj.netId }.orElse(-1)
    }

    fun getCraftingData(): MutableList<RecipeData> = this.craftingData

    fun getContainerMixData(): MutableList<ContainerMixData> = this.containerMixData

    fun getPotionMixData(): MutableList<PotionMixData> = this.potionMixData
}