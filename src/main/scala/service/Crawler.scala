package service

import org.jsoup.Jsoup
import routes.crawler.UrlTitle
import sttp.client3.quick.backend
import sttp.client3.{UriContext, basicRequest}
import zio.{Task, ZIO}

object Crawler {
  def getUrlTitles(urls: Seq[String]): Task[Seq[UrlTitle]] =
    ZIO.collectAllPar(urls.distinct.map(getTitleFromUrl))

  private def getTitleFromUrl(url: String): Task[UrlTitle] =
    ZIO
      .attempt(basicRequest.get(uri"$url").send(backend))
      .foldZIO(
        err => ZIO.succeed(UrlTitle(url, err.getMessage)),
        response =>
          response.body match {
            case Right(successResponse) =>
              ZIO.succeed(UrlTitle(url, Jsoup.parse(successResponse).title))
            case Left(statusMessage) => ZIO.succeed(UrlTitle(url, statusMessage))
          }
      )
}
