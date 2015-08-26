import sbt._
import Keys._

object SbtBowerBuild extends Build with BuildExtra {

  lazy val sbtBower = Project("sbt-bower", file("."), settings = mainSettings)

	lazy val mainSettings: Seq[Def.Setting[_]] = Defaults.defaultSettings ++ Seq(
		sbtPlugin := true,
		name := "sbt-bower",
		organization := "org.mdedetrich",
		version := "0.2.1",
		scalacOptions ++= Seq("-deprecation", "-unchecked"),
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-jackson" % "3.2.8"
    ),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := <url>https://github.com/mdedetrich/sbt-bower</url>
      <licenses>
        <license>
          <name>BSD-style</name>
          <url>http://www.opensource.org/licenses/bsd-license.php</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:mdedetrich/utforsca.git</url>
        <connection>scm:git:git@github.com:mdedetrich/sbt-bower.git</connection>
      </scm>
      <developers>
        <developer>
          <id>mdedetrich</id>
          <name>Matthew de Detrich</name>
          <email>mdedetrich@gmail.com</email>
        </developer>
      </developers>
  )
}