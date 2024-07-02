package config

import config.Config.Application
import zio.ZLayer
import zio.config._
import zio.config.magnolia.descriptor
import zio.config.typesafe.TypesafeConfigSource

case class Config(
    app: Application
)

object Config {
  case class Application(
      applicationName: String,
      applicationVersion: String,
      port: Int,
      threads: Int,
      maxRequestSize: Int,
      swaggerEnabled: Boolean,
      role: String
  )

  val live: ZLayer[Any, ReadError[String], Config] =
    ZLayer {
      read {
        descriptor[Config].from(
          TypesafeConfigSource.fromResourcePath
            .at(PropertyTreePath.$("Config"))
        )
      }
    }
}
