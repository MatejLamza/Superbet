package matej.lamza.core_model.mapper

import matej.lamza.core_model.Betshop
import matej.lamza.core_network.model.BetshopResponse

//TODO: maybe remove this outside so this layer is not dependant on core-network module?
object BetshopDomainMapper : DomainMapper<List<Betshop>, List<BetshopResponse>> {
    override fun asDomain(entity: List<BetshopResponse>): List<Betshop> {
        return entity.map { response ->
            return@map with(response) {
                return@with Betshop(
                    name, county, city, address
                )
            }
        }
    }
}

fun List<BetshopResponse>.asDomain(): List<Betshop> {
    return BetshopDomainMapper.asDomain(this)
}
