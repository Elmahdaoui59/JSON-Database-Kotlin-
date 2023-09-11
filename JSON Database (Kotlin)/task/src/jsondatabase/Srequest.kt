package jsondatabase

import kotlinx.serialization.Serializable

@Serializable
data class Srequest(
    val type: String? = null,
    val key: String? = null,
    val value: String? = null
)
