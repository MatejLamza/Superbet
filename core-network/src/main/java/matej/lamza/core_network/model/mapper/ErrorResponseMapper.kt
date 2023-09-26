package matej.lamza.core_network.model.mapper

import com.skydoves.sandwich.ApiErrorModelMapper
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.message
import matej.lamza.core_network.model.BetshopErrorResponse

object ErrorResponseMapper : ApiErrorModelMapper<BetshopErrorResponse> {

    /**
     *  Maps the [ApiResponse.Failure.Error] to the [BetshopErrorResponse] using the mapper.
     */
    override fun map(apiErrorResponse: ApiResponse.Failure.Error<*>): BetshopErrorResponse {
        return BetshopErrorResponse(apiErrorResponse.statusCode.code, apiErrorResponse.message())
    }
}
