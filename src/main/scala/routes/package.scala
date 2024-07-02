import routes.crawler.UrlTitle
import sttp.tapir.EndpointIO
import sttp.tapir.EndpointIO.Example
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto._
import io.circe.generic.auto._

package object routes {
  val getUrlOutput: EndpointIO.Body[String, Seq[UrlTitle]] = jsonBody[Seq[UrlTitle]].example(
    Example(
      Seq(
        UrlTitle("https://www.google.com/", "Google"),
        UrlTitle("https://www.ya.ru/", "Яндекс — быстрый поиск в интернете")
      ),
      Some("Example Response"),
      Some("Example Response")
    )
  )

  val urlsPath: EndpointIO.Body[String, Seq[String]] = jsonBody[Seq[String]]
    .description("A list of urls for which we want to get names")
    .example(Example(Seq("google.com", "yandex.ru"), Some("Url examples"), Some("Example urls")))

}
