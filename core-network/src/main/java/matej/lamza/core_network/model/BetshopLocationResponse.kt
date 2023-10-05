package matej.lamza.core_network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BetshopLocationResponse(
    @field:Json(name = "lng") val longitude: Double,
    @field:Json(name = "lat") val latitude: Double,
)
