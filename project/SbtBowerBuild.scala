import sbt._
import Keys._

object SbtBowerBuild extends Build with BuildExtra {

	lazy val sbtBower = Project("sbt-bower", file("."), settings = mainSettings)

	lazy val mainSettings: Seq[Project.Setting[_]] = Defaults.defaultSettings ++ ScriptedPlugin.scriptedSettings ++ Seq(
		sbtPlugin := true,
		name := "sbt-bower",
		organization := "com.github.masseguillaume",
		version := "0.1-SNAPSHOT",
		scalacOptions ++= Seq("-deprecation", "-unchecked")
	)
}