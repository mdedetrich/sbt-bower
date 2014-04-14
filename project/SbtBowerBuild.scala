import sbt._
import Keys._

object SbtBowerBuild extends Build with BuildExtra {

  lazy val sbtBower = Project("sbt-bower", file("."), settings = mainSettings)

	lazy val mainSettings: Seq[Def.Setting[_]] = Defaults.defaultSettings ++ Seq(
		sbtPlugin := true,
		name := "sbt-bower",
		organization := "com.mdedetrich",
		version := "0.2.1",
		scalacOptions ++= Seq("-deprecation", "-unchecked"),
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-jackson" % "3.2.8"
    )
  )
}