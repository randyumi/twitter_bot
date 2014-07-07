package randyumi.external.api.exception

class ExternalApiErrorException(message: String = "ExternalApiFailed", cause: Exception = null)
  extends Exception(message, cause)
