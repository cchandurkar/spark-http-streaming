import Dependencies._

ThisBuild / scalaVersion     := "2.11.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "me.cchandurkar"

// Add Resolvers
resolvers ++= Seq(
  "bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/",
  "Central" at "https://repo1.maven.org/maven2/"
)

// Spark Dependencies
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.5" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.5"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.4.5"

// App
lazy val root = (project in file("."))
  .settings(
    name := "Spark Http Streaming",
    libraryDependencies += scalaTest % Test
  )

