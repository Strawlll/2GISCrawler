import sttp.tapir.server.interceptor.cors.{CORSConfig, CORSInterceptor}
import sttp.tapir.server.interceptor.log.DefaultServerLog
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import zio.{Task, ZIO}

object ServerOptions {
  def errorLog(msg: String, error: Option[Throwable]): Task[Unit] =
    error match {
      case None    => ZIO.logInfo(msg)
      case Some(_) => ZIO.logError(s"$msg; $error")
    }

  def decodeLog(msg: String, error: Option[Throwable]): Task[Unit] =
    error match {
      case None    => ZIO.logError(msg)
      case Some(_) => ZIO.logError(s"$msg; $error")
    }

  val corsConfig = CORSConfig.default

  val cors = CORSInterceptor.customOrThrow[Task](corsConfig)
  val serverOptions: ZioHttpServerOptions[Any] =
    ZioHttpServerOptions.customiseInterceptors
      .serverLog(
        DefaultServerLog[Task](
          doLogWhenReceived = msg => ZIO.logInfo(s"$msg"),
          doLogWhenHandled = errorLog,
          doLogAllDecodeFailures = decodeLog,
          doLogExceptions = (msg: String, ex: Throwable) => ZIO.logError(s"$msg; $ex"),
          noLog = ZIO.unit
        )
      )
      .corsInterceptor(cors)
      .options
}
