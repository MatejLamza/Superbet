package matej.lamza.core_network.service

import com.skydoves.sandwich.ApiResponse
import matej.lamza.core_network.model.BetshopPageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BetshopService {
    @GET("/betshops?boundingBox={0}")
    suspend fun fetchBetshops(
        @Query("boundingBox") boundingBox: String
    ): ApiResponse<BetshopPageResponse>
}
