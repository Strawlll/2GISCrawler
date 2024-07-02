package routes.crawler

import config.Config
import routes.RootEndpoint

abstract class BaseCrawlerApi(config: Config) extends RootEndpoint(config) {
  val baseCrawlerEndpoint =
    rootEndpoint
      .in("crawler")
      .tag("crawler")
}
