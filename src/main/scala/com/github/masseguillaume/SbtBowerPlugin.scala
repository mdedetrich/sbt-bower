package com.github.masseguillaume

import sbt._
import Keys._

object SbtBowerPlugin extends Plugin {
	object BowerKeys {
		val frontendDependencies = SettingKey[Seq[FrontendDependency]]("frontend-dependency")
	}

	lazy val bowerSettings: Seq[Setting[_]] = Seq(
    BowerKeys.frontendDependencies := Seq.empty
	)

  override lazy val projectSettings = Seq( commands ++= Seq( install, list, search, register, uninstall ) )

  val bower = "bower"

	val install = Command.command("bower-install") { state =>

    import scala.sys.process._

    val project = Project.extract(state)
    for { dependencies <- project.getOpt(BowerKeys.frontendDependencies)
          dependency <- dependencies } {

      ( bower + " install " + dependency.install).!
    }

    state
	}

	val list = notImplemented("list")
	val search = notImplemented("search")
	val register = notImplemented("register")
	val uninstall = notImplemented("uninstall")

	private def notImplemented(commandName: String) = Command.command( commandName ) { identity }

  implicit def toFrontendDependency( artifactName: String ) = new FrontendDependency( artifactName )
}

class FrontendDependency( artifactName: String ) {
	def `#` ( revision: String ) = new FrontendDependencyWithRevision( artifactName, revision )
	def install = artifactName
}

class FrontendDependencyWithRevision( artifactName: String, revision: String ) extends FrontendDependency( artifactName ) {
  override def install = super.install+ " # " + revision
}
