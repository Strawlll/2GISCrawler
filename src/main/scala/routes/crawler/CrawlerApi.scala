package routes.crawler

import config.Config
import errors.ResponseError.BadRequest
import routes.{getUrlOutput, urlsPath}
import service.Crawler.getUrlTitles
import sttp.model.StatusCode
import sttp.tapir.EndpointIO.Example
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.interceptor.RequestResult.Response
import sttp.tapir.ztapir._
import sttp.tapir.{EndpointInput, path}
import zio.{Task, ZIO, ZLayer}

sealed case class CrawlerApi(config: Config) extends BaseCrawlerApi(config) {
  def getUrlName =
    baseCrawlerEndpoint.post
      .in(urlsPath)
      .errorOutVariant(
        oneOfVariant(statusCode(StatusCode.BadRequest).and(stringBody.mapTo[BadRequest]))
      )
      .out(getUrlOutput)
      .description("Return urls names")

  def getUrlNameLogic: Seq[String] => ZIO[Any, BadRequest, Seq[UrlTitle]] =
    urls =>
      getUrlTitles(urls).foldZIO(
        err => ZIO.succeed(log.error(err.getMessage)) *> ZIO.fail(BadRequest(err.getMessage)),
        success => ZIO.succeed(success)
      )

  def routes: List[ZServerEndpoint[Any, Any]] = List(
    getUrlName.serverLogic(_ => getUrlNameLogic).asInstanceOf[ZServerEndpoint[Any, Any]]
  )
}

object CrawlerApi {
  lazy val live: ZLayer[Config, Any, CrawlerApi] =
    ZLayer.fromFunction(CrawlerApi.apply _)
}
