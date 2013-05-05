import sbt._
import Keys._

object SbtBowerBuild extends Build with BuildExtra {

	lazy val sbtBower = Project("sbt-bower", file("."), settings = mainSettings)

	lazy val mainSettings: Seq[Project.Setting[_]] = Defaults.defaultSettings ++ /*ScriptedPlugin.scriptedSettings ++ */ Seq(
		sbtPlugin := true,
		name := "sbt-bower",
		organization := "com.github.masseguillaume",
		version := "0.2.0-SNAPSHOT",
		scalacOptions ++= Seq("-deprecation", "-unchecked"),
		publishTo := Some(Resolver.file("Github Pages", 
			Path.userHome / "Project" / "Github" / "masseguillaume.github.com" / "maven" asFile)(
			Patterns(true, Resolver.mavenStyleBasePattern))
		),
		publishMavenStyle := true,
    	publishArtifact in Test := false,
    	pomIncludeRepository := (_ => false),
    	pomExtra := extraPom
	)

	def extraPom = (
	   <url></url>
	    <licenses>
	      <license>
	        <name>Apache 2.0</name>
	        <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
	        <distribution>repo</distribution>
	      </license>
	    </licenses>
	    <scm>
	      <url>git@github.com:MasseGuillaume/sbt-bower.git</url>
	      <connection>scm:git:git@github.com:MasseGuillaume/sbt-bower.git</connection>
	    </scm>
	    <developers>
	      <developer>
	      <id>MasseGuillaume</id>
	      <name>Guillaume Massé</name>
	      <url>http://github.com/MasseGuillaume</url>
	    </developer>
	  </developers>
	)
}