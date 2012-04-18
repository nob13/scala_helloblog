import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "helloblog"
    val appVersion      = "1.0"

    val appDependencies = Seq(
            // Add your project dependencies here,
    		"eu.henkelmann" %% "actuarius" % "0.2.3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
      resolvers += "Actuarius repository" at "http://maven.henkelmann.eu/"
    )

}
