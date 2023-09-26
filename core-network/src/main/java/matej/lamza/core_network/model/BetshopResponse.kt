package matej.lamza.core_network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BetshopResponse(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "county") val county: String,
    @field:Json(name = "city_id") val cityId: Long,
    @field:Json(name = "city") val city: String,
    @field:Json(name = "address") val address: String,
)
