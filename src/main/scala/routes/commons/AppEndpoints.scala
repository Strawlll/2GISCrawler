package routes.commons

import config.Config
import routes.crawler.CrawlerApi
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zio.{Task, ZLayer}

case class AppEndpoints(crawlerApi: CrawlerApi, config: Config) {
  val allEndpoints: List[ZServerEndpoint[Any, Any]] =
    if (config.app.swaggerEnabled) crawlerApi.routes ++ docsEndpoints(crawlerApi.routes)
    else crawlerApi.routes

  private def docsEndpoints(
      apiEndpoints: List[ZServerEndpoint[Any, Any]]
  ): List[ZServerEndpoint[Any, Any]] =
    SwaggerInterpreter().fromServerEndpoints[Task](
      apiEndpoints,
      config.app.applicationName,
      config.app.applicationVersion
    )
}

object AppEndpoints {
  lazy val live: ZLayer[
    CrawlerApi with Config,
    Nothing,
    AppEndpoints
  ] = ZLayer.fromFunction(AppEndpoints.apply _)
}
