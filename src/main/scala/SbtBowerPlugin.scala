import sbt._
import Keys._
import Path._
import IO._


object BowerKeys {
  val Bower = config("bower") extend (Compile)
  val frontendDependencies = SettingKey[Seq[FrontendDependency]]("frontend-dependency","frontend dependencies to resolve with bower")
  val bowerPath = SettingKey[String]("bower-path","where bower is installed")
}

object SbtBowerPlugin extends Plugin {

  import BowerKeys._

  implicit def toFrontendDependency( artifactName: String ) = new FrontendDependency( artifactName )

  val install = TaskKey[Unit]("install","install frontend dependencies")
	private def installTask: Def.Initialize[Task[Unit]] = (bowerPath, frontendDependencies, sourceDirectory, streams ) map { (bower, dependencies, source, s ) =>
	  for { dependency <- dependencies } {
	    s.log.info("installing %s".format(dependency.install) )
      createDirectory( source )
	    Process( bower :: "install" :: dependency.install :: Nil, source ) ! s.log
	  }
	}

  val list = TaskKey[Unit]("list","list all the packages that are installed locally")
  private def listTask: Def.Initialize[Task[Unit]] = (bowerPath, streams) map { (bower, s) =>
    Process( bower :: "install" :: Nil ) ! s.log
  }

  lazy val bowerSettings: Seq[Setting[_]] = Seq(
    libraryDependencies in Bower := Seq.empty,
    frontendDependencies := Seq.empty,
    bowerPath := "/usr/local/share/npm/bin/bower",
    sourceDirectory in Bower <<= sourceDirectory.apply (_ / "main" / "webapp" )
  )

  override lazy val settings: Seq[Setting[_]] = inConfig(Bower) (Seq (
    install <<= installTask,
    list <<= listTask
  ))

}

class FrontendDependency( artifactName: String ) {
	def `#` ( revision: String ) = new FrontendDependencyWithRevision( artifactName, revision )
	def HEAD = this
	def install = artifactName
}

class FrontendDependencyWithRevision( artifactName: String, revision: String ) extends FrontendDependency( artifactName ) {
  override def install = "%s#%s".format( super.install, revision )
}
