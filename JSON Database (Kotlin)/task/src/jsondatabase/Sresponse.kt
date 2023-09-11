package jsondatabase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Sresponse(
    val response: String? = null,
    val value: JsonElement? = null,
    val reason: String? = null
)
