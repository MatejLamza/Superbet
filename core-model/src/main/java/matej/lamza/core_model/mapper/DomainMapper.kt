package matej.lamza.core_model.mapper

interface DomainMapper<Domain, Response> {

    fun asDomain(response: Response): Domain
}
