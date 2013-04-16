import sbt._
import Keys._

import com.github.masseguillaume.SbtBowerPlugin._
import BowerKeys._

object SimpleBuild extends Build {
	lazy val simple = Project(
		id = "Simple",
		base = file("."),
		settings = Project.defaultSettings ++ bowerSettings ++ Seq(
      frontendDependencies ++= Seq(
				"jquery" `#` "1.9.1",
				"d3" `#` "v3.1.5",
				"threejs" `#` "r57",
				"bootstrap" `#` "v2.3.1"
			)
		)
	)
}