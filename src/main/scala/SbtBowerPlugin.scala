import sbt._
import Keys._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._

object BowerKeys {
  val Bower = config("bower") extend Compile
  val frontendDependencies = SettingKey[Seq[FrontendDependency]]("frontend-dependency","frontend dependencies to resolve with bower")
  val installDirectory = SettingKey[File]("install-directory","where js libraries are installed relative to source directory")
}

object SbtBowerPlugin extends Plugin {

  import BowerKeys._

  implicit def toFrontendDependency( artifactName: String ) = new FrontendDependency( artifactName )


  lazy val setupFilesTask = Def.task {
    val bowerRC = (sourceDirectory in Bower).value / ".bowerrc"
    val bowerJSON = (sourceDirectory in Bower).value / "bower.json"
    val installDirectoryPath = (sourceDirectory in Bower).value.relativize((installDirectory in Bower).value)
    val fileContents =
      ("directory" -> (installDirectoryPath.head.getPath))
    IO.write(bowerRC,compact(render(fileContents)))
    val dependencies = JObject(frontendDependencies.value.map(_.install).toList)
    val json =(
      ("name" -> name.value) ~
        ("version" -> version.value) ~
        ("devDependencies" -> dependencies)
      )
    IO.write(bowerJSON,compact(render(json)))
    (bowerRC,bowerJSON)
  }

  lazy val installTask = Def.task {
    val files = setupFilesTask.value
    val (bowerRC,bowerJSON) = files
    streams.value.log.info("Checking/installing bower dependencies")
    Process( "bower" :: "install" :: Nil, (sourceDirectory in Bower).value ) ! streams.value.log
    IO.delete(bowerRC)
    IO.delete(bowerJSON)
  }

  val install = TaskKey[Unit]("install","install frontend dependencies")


  lazy val listTask = Def.task {
    val files = setupFilesTask.value
    val (bowerRC,bowerJSON) = files
    streams.value.log.info("Listing bower dependencies")
    Process("bower" :: "list" :: Nil, (sourceDirectory in Bower).value) ! streams.value.log
    IO.delete(bowerRC)
    IO.delete(bowerJSON)
  }

  val list = TaskKey[Unit]("list","list all the packages that are installed locally")
  val rawr = TaskKey[Unit]("rawr", "list rawr")


  lazy val bowerSettings: Seq[Setting[_]] = Seq(
    libraryDependencies in Bower := Seq.empty,
    frontendDependencies := Seq.empty,
    sourceDirectory in Bower <<= sourceDirectory (_ / "main" / "webapp" ),
    installDirectory in Bower <<= (sourceDirectory in Bower) (_ / "js" / "lib"),
    install in Bower := installTask.value,
    list in Bower := listTask.value
  )
}

class FrontendDependency( artifactName: String) {
	def %%% ( revision: String ) = new FrontendDependencyWithRevision(artifactName, revision )
  def install:JField = throw new IllegalArgumentException("Must provide a version")
}

class FrontendDependencyWithRevision( artifactName: String, revision: String ) extends FrontendDependency( artifactName ) {
  override def install = JField(artifactName,JString(revision))
}
