package routes

import config.Config
import errors.ResponseError._
import errors._
import org.slf4j.{Logger, LoggerFactory}
import sttp.model.StatusCode
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir._
import zio.{IO, ZIO}

abstract class RootEndpoint(config: Config) {
  val log: Logger = LoggerFactory.getLogger(ZioHttpInterpreter.getClass.getName)
  private def hasRole(roles: Option[String]): IO[ResponseError, String] =
    roles
      .map(r =>
        if (r.split(",").contains(config.app.role)) ZIO.succeed(config.app.role)
        else ZIO.fail(Forbidden("Bad role list"))
      )
      .getOrElse(ZIO.fail(Unauthorized("Role list was not provided")))

  val rootEndpoint
      : ZPartialServerEndpoint[Nothing, Option[String], String, Unit, ResponseError, Unit, Any] =
    endpoint
      .securityIn(header[Option[String]]("token"))
      .errorOut(
        oneOf(
          oneOfVariant(statusCode(StatusCode.Forbidden).and(stringBody.mapTo[Forbidden])),
          oneOfVariant(statusCode(StatusCode.Unauthorized).and(stringBody.mapTo[Unauthorized])),
          oneOfVariant(statusCode(StatusCode.BadRequest).and(stringBody.mapTo[BadRequest])),
          oneOfDefaultVariant(
            statusCode(StatusCode.InternalServerError).and(stringBody.mapTo[InternalServerError])
          )
        )
      )
      .zServerSecurityLogic(hasRole)
}
