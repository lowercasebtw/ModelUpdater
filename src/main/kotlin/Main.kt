package btw.lowercase.modelupdater

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.Strictness
import java.nio.file.Path

val BOW = """
{
    "overrides": [
        {
            "predicate": {
                "pulling": 1
            },
            "model": "item/bow_pulling_0"
        },
        {
            "predicate": {
                "pulling": 1,
                "pull": 0.65
            },
            "model": "item/bow_pulling_1"
        },
        {
            "predicate": {
                "pulling": 1,
                "pull": 0.9
            },
            "model": "item/bow_pulling_2"
        }
    ]
}
""".trimIndent()

val CLOCK = """
{
    "overrides": [
        {
            "predicate": {
                "time": 0
            },
            "model": "item/clock"
        },
        {
            "predicate": {
                "time": 0.0078125
            },
            "model": "item/clock_01"
        },
        {
            "predicate": {
                "time": 0.0234375
            },
            "model": "item/clock_02"
        },
        {
            "predicate": {
                "time": 0.0390625
            },
            "model": "item/clock_03"
        },
        {
            "predicate": {
                "time": 0.0546875
            },
            "model": "item/clock_04"
        },
        {
            "predicate": {
                "time": 0.0703125
            },
            "model": "item/clock_05"
        },
        {
            "predicate": {
                "time": 0.0859375
            },
            "model": "item/clock_06"
        },
        {
            "predicate": {
                "time": 0.1015625
            },
            "model": "item/clock_07"
        },
        {
            "predicate": {
                "time": 0.1171875
            },
            "model": "item/clock_08"
        },
        {
            "predicate": {
                "time": 0.1328125
            },
            "model": "item/clock_09"
        },
        {
            "predicate": {
                "time": 0.1484375
            },
            "model": "item/clock_10"
        },
        {
            "predicate": {
                "time": 0.1640625
            },
            "model": "item/clock_11"
        },
        {
            "predicate": {
                "time": 0.1796875
            },
            "model": "item/clock_12"
        },
        {
            "predicate": {
                "time": 0.1953125
            },
            "model": "item/clock_13"
        },
        {
            "predicate": {
                "time": 0.2109375
            },
            "model": "item/clock_14"
        },
        {
            "predicate": {
                "time": 0.2265625
            },
            "model": "item/clock_15"
        },
        {
            "predicate": {
                "time": 0.2421875
            },
            "model": "item/clock_16"
        },
        {
            "predicate": {
                "time": 0.2578125
            },
            "model": "item/clock_17"
        },
        {
            "predicate": {
                "time": 0.2734375
            },
            "model": "item/clock_18"
        },
        {
            "predicate": {
                "time": 0.2890625
            },
            "model": "item/clock_19"
        },
        {
            "predicate": {
                "time": 0.3046875
            },
            "model": "item/clock_20"
        },
        {
            "predicate": {
                "time": 0.3203125
            },
            "model": "item/clock_21"
        },
        {
            "predicate": {
                "time": 0.3359375
            },
            "model": "item/clock_22"
        },
        {
            "predicate": {
                "time": 0.3515625
            },
            "model": "item/clock_23"
        },
        {
            "predicate": {
                "time": 0.3671875
            },
            "model": "item/clock_24"
        },
        {
            "predicate": {
                "time": 0.3828125
            },
            "model": "item/clock_25"
        },
        {
            "predicate": {
                "time": 0.3984375
            },
            "model": "item/clock_26"
        },
        {
            "predicate": {
                "time": 0.4140625
            },
            "model": "item/clock_27"
        },
        {
            "predicate": {
                "time": 0.4296875
            },
            "model": "item/clock_28"
        },
        {
            "predicate": {
                "time": 0.4453125
            },
            "model": "item/clock_29"
        },
        {
            "predicate": {
                "time": 0.4609375
            },
            "model": "item/clock_30"
        }
    ]
}
""".trimIndent()

// TODO: Handle special model types, like shields, etc
val SHIELD = """
{
    "overrides": [
        {
            "predicate": {
                "blocking": 1
            },
            "model": "item/shield_blocking"
        }
    ]
}
""".trimIndent()

class ModelUpdater(val pack_path: Path) {
    val gson = GsonBuilder().setPrettyPrinting().setStrictness(Strictness.LENIENT).create()

    private fun addFallback(model: JsonObject, name: String) {
        model.addProperty("type", "minecraft:model")
        model.addProperty("model", name)
    }

    private fun test(name: String, inputModel: JsonObject): JsonObject {
        val model = JsonObject()
        if (inputModel.has("overrides")) {
            val overrides = inputModel.remove("overrides")
            if (!overrides.isJsonArray) {
                throw Exception("Overrides is a invalid object type! Expected array.")
            }

            model.addProperty("model", "minecraft:select")
            
            val cases = JsonArray()
            var last_type: String? = null
            for (element in overrides.asJsonArray) {
                if (!element.isJsonObject) {
                    println("Encountered a non-json object inside overrides array, skipping..")
                    continue
                }

                val obj = element.asJsonObject
                if (!obj.has("model")) {
                    println("Override is missing target model, skipping..")
                    continue
                }

                val modelElement = obj.get("model")
                if (!modelElement.isJsonPrimitive) {
                    println("Override is missing target model, skipping..")
                    continue
                }

                val modelPrimitive = modelElement.asJsonPrimitive
                if (!modelPrimitive.isString) {
                    println("Override is missing target model, skipping..")
                    continue
                }

                val targetModel = modelPrimitive.asString
                if (obj.has("predicate")) {
                    val predicateElement = obj.get("predicate")
                    if (!predicateElement.isJsonObject) {
                        println("Override predicate is not a object, skipping until I find one..")
                        continue
                    } else {
                        val predicate = predicateElement.asJsonObject
                        var case = JsonObject()
                        for (entry in predicate.asMap()) {
                            val type = when (entry.key) {
                                "angle", "cooldown", "damage", "damaged", "lefthanded", "charged", "firework", "throwing", "level", "filled", "tooting", "trim_type", "brushing", "honey_level" -> TODO()
                                "pull", "time" -> "minecraft:range_dispatch"
                                "pulling", "blocking" -> "minecraft:using_item" // Special handling based on item type
                                "custom_model_data" -> "minecraft:custom_model_data"
                                "cast" -> "minecraft:conditional" // Special Handling based fishing rod
                                else -> TODO("unknown predicate type")
                            }

                            if (last_type != null && type == "minecraft:using_item" && last_type.equals(type)) {
                                continue
                            }

                            if (last_type == "minecraft:using_item") {
                                // modify to contain the new condition
                                error("NEED TO MODIFY USING ITEM CONDITION")
                            } else if (last_type == "minecraft:range_dispatch") {
                                error("NEED TO MODIFY USING RANGE DISPATCH")
                            }
                            println("type: $type, last_type: $last_type")

                            when (type) {
                                "minecraft:using_item" -> {
                                    val newCase = JsonObject()
                                    newCase.addProperty("type", "minecraft:conditional")

                                    val onFalse = JsonObject()
                                    addFallback(onFalse, name)
                                    newCase.add("on_false", onFalse)

                                    val onTrue = JsonObject()
                                    onTrue.addProperty("type", "minecraft:model")
                                    onTrue.addProperty("model", targetModel)
                                    newCase.add("on_true", onTrue)

                                    newCase.addProperty("property", type)
                                    case = newCase
                                }

                                "minecraft:range_dispatch" -> {
                                    val newCase = JsonObject()
                                    newCase.addProperty("type", "minecraft:range_dispatch")
                                    case = newCase
                                }

                                else -> TODO("handle type $type")
                            }

                            last_type = type
                            cases.add(case)
                        }
                    }
                }
            }

            model.add("cases", cases)
            run {
                val fallback = JsonObject()
                addFallback(fallback, name)
                model.add("fallback", fallback)
            }
        } else {
            addFallback(model, name)
        }

        return model
    }

    fun update() {
        // TODO: real code
        println(gson.toJson(test("minecraft:item/clock", gson.fromJson(CLOCK, JsonObject().javaClass))))
    }
}

fun main() {
    ModelUpdater(Path.of("/pack/")).update()
}