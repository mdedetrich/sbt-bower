package bower

import sbt._
import sbt.Keys._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import complete.DefaultParsers._
import sbt.complete.Parser

import scala.language.implicitConversions

object Keys {
  import SbtBowerPlugin.FrontendDependency
  import SbtBowerPlugin.ScriptType
  import SbtBowerPlugin.ScriptDefinition

  val Bower = config("bower") extend Compile
  val frontendDependencies = SettingKey[Seq[FrontendDependency]]("frontend-dependency","frontend dependencies to resolve with bower")
  val installDirectory = SettingKey[File]("install-directory","where js libraries are installed relative to source directory")
  val hookScripts = SettingKey[Seq[ScriptDefinition]]("bower-scripts", "bower script hooks")

  val PostInstall = ScriptType.PostInstall
  val PreInstall = ScriptType.PreInstall
  val PreUnInstall = ScriptType.PreUnInstall
}

object SbtBowerPlugin extends Plugin {

  import bower.Keys._

  lazy val setupFilesTask = Def.task {
    val bowerRC = (sourceDirectory in Bower).value / ".bowerrc"
    val bowerJSON = (sourceDirectory in Bower).value / "bower.json"
    val installDirectoryPath = (sourceDirectory in Bower).value.relativize((installDirectory in Bower).value)
    val fileContents = JObject(
      "directory" -> installDirectoryPath.head.getPath,
      "scripts" -> JObject((hookScripts in Bower).value.map(_.evaluate).toList)
    )
    IO.write(bowerRC,compact(render(fileContents)))
    val dependencies = JObject(frontendDependencies.value.map(_.install).toList)
    val json = ("name" -> name.value) ~
      ("version" -> version.value) ~
      ("devDependencies" -> dependencies)
    IO.write(bowerJSON,compact(render(json)))
    (bowerRC,bowerJSON)
  }

  lazy val installTask = Def.task {
    val files = setupFilesTask.value
    val (bowerRC,bowerJSON) = files
    streams.value.log.info("Checking/installing frontendDependencies")
    Process( "bower" :: "install" :: Nil, (sourceDirectory in Bower).value ) ! streams.value.log
    IO.delete(bowerRC)
    IO.delete(bowerJSON)
  }

  val install = TaskKey[Unit]("install","install frontendDependencies")

  lazy val listTask = Def.task {
    val files = setupFilesTask.value
    val (bowerRC,bowerJSON) = files
    streams.value.log.info("Listing bower dependencies")
    Process("bower" :: "list" :: Nil, (sourceDirectory in Bower).value) ! streams.value.log
    IO.delete(bowerRC)
    IO.delete(bowerJSON)
  }

  val list = TaskKey[Unit]("list","list all the packages that are installed in installDirectory")

  lazy val pruneTask = Def.task {
    val files = setupFilesTask.value
    val (bowerRC,bowerJSON) = files
    streams.value.log.info("Pruning frontendDependencies")
    Process("bower" :: "prune" :: Nil,(sourceDirectory in Bower).value) ! streams.value.log
    IO.delete(bowerRC)
    IO.delete(bowerJSON)
  }

  val prune = TaskKey[Unit]("prune","removes packages from installDirectory that no longer exist in frontendDependencies")

  val packageQuery: Parser[String] = Space ~> StringBasic.examples("<package>")

  lazy val searchTask = Def.inputTask {
    val files = setupFilesTask.value
    val (bowerRC,bowerJSON) = files
    val query: String = packageQuery.parsed
    Process("bower" :: "search" :: query :: Nil,(sourceDirectory in Bower).value) ! streams.value.log
    IO.delete(bowerRC)
    IO.delete(bowerJSON)
  }

  val search = InputKey[Unit]("search","searches bower packages")

  lazy val infoTask = Def.inputTask {
    val files = setupFilesTask.value
    val (bowerRC,bowerJSON) = files
    val query:String = packageQuery.parsed
    Process("bower" :: "info" :: query :: Nil,(sourceDirectory in Bower).value) ! streams.value.log
    IO.delete(bowerRC)
    IO.delete(bowerJSON)
  }

  lazy val info = InputKey[Unit]("info","provides info on a bower package")

  lazy val uninstallTask = Def.inputTask {
    val files = setupFilesTask.value
    val (bowerRC,bowerJSON) = files
    val query:String = packageQuery.parsed
    Process("bower" :: "uninstall" :: query :: Nil,(sourceDirectory in Bower).value) ! streams.value.log
    IO.delete(bowerRC)
    IO.delete(bowerJSON)
  }

  val uninstall = InputKey[Unit]("uninstall","uninstall a bower package")

  lazy val bowerSettings: Seq[Setting[_]] = Seq(
    libraryDependencies in Bower := Seq.empty,
    frontendDependencies := Seq.empty,
    hookScripts in Bower := Seq.empty,
    sourceDirectory in Bower <<= sourceDirectory (_ / "main" / "webapp" ),
    installDirectory in Bower <<= (sourceDirectory in Bower) (_ / "js" / "lib"),
    install in Bower := installTask.value,
    list in Bower := listTask.value,
    prune in Bower := pruneTask.value,
    search in Bower := searchTask.value.evaluated,
    info in Bower := infoTask.value.evaluated,
    uninstall in Bower := uninstallTask.value.evaluated
  )

  class ScriptDefinition(scriptType: ScriptType, value: String) {
    def evaluate = JField(scriptType.value, value)
  }

  sealed trait ScriptType {
    val value: String
    def :=(script: String) = new ScriptDefinition(this, script)
  }

  object ScriptType {
    case object PreInstall extends ScriptType { val value = "preinstall" }
    case object PostInstall extends ScriptType { val value = "postinstall" }
    case object PreUnInstall extends ScriptType { val value = "preuninstall" }
  }

  final implicit def toFrontendDependency( artifactName: String ): FrontendDependency = new FrontendDependency( artifactName )

  class FrontendDependency( artifactName: String) {
    def %%% ( revision: String ) = new FrontendDependencyWithRevision(artifactName, revision )
    def install:JField = throw new IllegalArgumentException("Must provide a version")
  }

  final class FrontendDependencyWithRevision( artifactName: String, revision: String ) extends FrontendDependency( artifactName ) {
    override def install = JField(artifactName,JString(revision))
  }

}

