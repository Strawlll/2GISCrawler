package errors

sealed trait ResponseError extends Product with Serializable

object ResponseError {
  case class InternalServerError(message: String) extends ResponseError

  case class Unauthorized(message: String) extends ResponseError

  case class Forbidden(message: String) extends ResponseError

  case class NotFound(message: String) extends ResponseError

  case class BadRequest(message: String) extends ResponseError
}
