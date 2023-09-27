package matej.lamza.core_model.exceptions

class MissingGPSException(override val message: String? = "GPS Location services are not enabled.") : Exception()
