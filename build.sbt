import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}


lazy val exp4j = crossProject(JVMPlatform, JSPlatform /*, NativePlatform*/)
  .crossType(CrossType.Full)
//  .crossType(new CrossType {
//    @deprecated
//    def projectDir(crossBase: File, projectType: String): File =
//      crossBase / projectType
//    def projectDir(crossBase: File, platform: sbtcrossproject.Platform): File =
//      crossBase / platform.identifier
//    def sharedSrcDir(projectBase: File, conf: String): Option[File] =
//      Some(projectBase.getParentFile / "src" / conf / "scala")
//  })
  .in(file("."))
  .settings(
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq(
//        "org.scalatest" %%% "scalatest" % "3.0.5" % Test,
        "junit" % "junit" % "4.11"
    ),
    organization := "net.objecthunter",
  )
  .jsSettings(
//    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
//    libraryDependencies += "com.lihaoyi" %%% "utest" % "0.6.3" % "test",
//    libraryDependencies += "com.softwaremill.sttp" %%% "core" % sttpVersion,
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
//      "org.scalatest" %%% "scalatest" % "3.0.5" % Test,
      "junit" % "junit" % "4.11",
//      crossPaths := false,
      "com.novocode" % "junit-interface" % "0.11" % Test,
      "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided",
    ),

  )

lazy val exp4jJS     = exp4j.js
lazy val exp4jJVM    = exp4j.jvm
//lazy val exp4jNative = exp4j.native
