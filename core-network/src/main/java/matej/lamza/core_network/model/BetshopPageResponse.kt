package matej.lamza.core_network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BetshopPageResponse(
    @field:Json(name = "count") val count: Int,
    @field:Json(name = "betshops") val betshops: List<BetshopResponse>
)
