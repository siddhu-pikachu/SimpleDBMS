ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "SimpleDBMS"
  )

libraryDependencies += "com.github.jsqlparser" % "jsqlparser" % "4.5"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test
