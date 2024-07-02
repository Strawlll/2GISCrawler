import ServerOptions.serverOptions
import config.Config
import routes.commons.AppEndpoints
import routes.crawler.CrawlerApi
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.http._
import zio.logging.backend.SLF4J
import zio.{ExitCode, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}


object Main extends ZIOAppDefault {
  override val bootstrap
      : ZLayer[ZIOAppArgs, Any, Any] = zio.Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  override def run: ZIO[Any, Any, ExitCode] =
    ZIO
      .service[Config]
      .flatMap(config =>
        (
          for {
            _ <- if (config.app.swaggerEnabled)
              ZIO.logInfo(s"Go to http://localhost:${config.app.port}/docs to open SwaggerUI.")
            else ZIO.logInfo(s"Go to http://localhost:${config.app.port} to use App.")
            endpoints <- ZIO.service[AppEndpoints]
            _         <- Server.serve(ZioHttpInterpreter(serverOptions).toHttp(endpoints.allEndpoints))
          } yield ()
        ).provide(
          ServerConfig.live(
            ServerConfig.default
              .port(config.app.port)
              .maxThreads(config.app.threads)
              .objectAggregator(config.app.maxRequestSize)
          ),
          AppEndpoints.live,
          CrawlerApi.live,
          Config.live,
          Server.live
        )
      )
      .provide(Config.live)
      .debug
      .exitCode
}
