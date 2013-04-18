import sbt._

import SbtBowerPlugin._

object SimpleBuild extends Build {
	lazy val simple = Project(
		id = "Simple",
		base = file("."),
		settings = Project.defaultSettings ++ SbtBowerPlugin.bowerSettings ++ Seq(
      BowerKeys.frontendDependencies ++= Seq(
				"jquery" `#` "1.9.1",
				"threejs" HEAD
			)
		)
	)
}