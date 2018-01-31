/* This build was created for get all lamda services with  exac dependencies. */

crossScalaVersions := Seq("2.12.4", scalaVersion.value)

addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.3")

developers := List(
  Developer(
    "fsaldivars",
    "Fidel Saldivar",
    "fsaldivars@gmail.com",
    url("https://github.com/fsaldivars")
  )
)

fork in Test := false

// define the statements initially evaluated when entering 'console', 'console-quick', or 'console-project'
initialCommands := """
                     |""".stripMargin

javacOptions ++=
  scalaVersion {
    case sv if sv.startsWith("2.10") => List(
      "-source", "1.7",
      "-target", "1.7"
    )

    case _ => List(
      "-source", "1.8",
      "-target", "1.8"
    )
  }.value ++ Seq(
    "-Xlint:deprecation",
    "-Xlint:unchecked",
    "-g:vars"
  )
//Recomended by scalatest  
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies ++= {
  val httpV = "4.4.1"
  Seq(
    "com.amazonaws"              %  "aws-java-sdk-sns"    	% "1.11.269" 	withSources(),
    "com.amazonaws" 				%  "aws-lambda-java-core" 	% "1.2.0"		withSources(),
    "org.slf4j"                  %  "slf4j-api"           	% "1.7.12"   	withSources() force(),
    "com.typesafe.akka" 			%% "akka-http" 			 	% "10.1.0-RC1",
    //
    "org.scalactic" 				%% "scalactic" 				% "3.0.4" 		%	"test",
	"org.scalatest" 				%% "scalatest" 				% "3.0.4" 		% 	"test",
	//"org.scalatestplus" %% "play" % "1.2.0" % Test,
    "junit"                      %  "junit"               	% "4.12"  		% Test
  )
}

assemblyMergeStrategy in assembly := {
    case PathList("com",   "esotericsoftware", xs @ _*) => MergeStrategy.last
    case PathList("com",   "squareup", xs @ _*) => MergeStrategy.last
    case PathList("com",   "sun", xs @ _*) => MergeStrategy.last
    case PathList("com",   "thoughtworks", xs @ _*) => MergeStrategy.last
    case PathList("commons-beanutils", xs @ _*) => MergeStrategy.last
    case PathList("commons-cli", xs @ _*) => MergeStrategy.last
    case PathList("commons-collections", xs @ _*) => MergeStrategy.last
    case PathList("commons-io", xs @ _*) => MergeStrategy.last
    case PathList("io",    "netty", xs @ _*) => MergeStrategy.last
    case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
    case PathList("javax", "xml", xs @ _*) => MergeStrategy.last
    case PathList("org",   "apache", xs @ _*) => MergeStrategy.last
    case PathList("org",   "codehaus", xs @ _*) => MergeStrategy.last
    case PathList("org",   "fusesource", xs @ _*) => MergeStrategy.last
    case PathList("org",   "mortbay", xs @ _*) => MergeStrategy.last
    case PathList("org",   "tukaani", xs @ _*) => MergeStrategy.last
    case PathList("xerces", xs @ _*) => MergeStrategy.last
    case PathList("xmlenc", xs @ _*) => MergeStrategy.last
    case "about.html" => MergeStrategy.rename
    case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
    case "META-INF/mailcap" => MergeStrategy.last
    case "META-INF/mimetypes.default" => MergeStrategy.last
    case "plugin.properties" => MergeStrategy.last
    case "log4j.properties" => MergeStrategy.last
    case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}
//This software is private
//licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

//updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)
logBuffered in Test := false

//logLevel := Level.Error

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
//logLevel in compile := Level.Warn
logLevel in test := Level.Info // Level.INFO is needed to see detailed output when running tests

name := "lambda-notification"

organization := "com.fsaldivars"

parallelExecution in Test := false


scalacOptions ++=
  scalaVersion {
    case sv if sv.startsWith("2.10") => List(
      "-target:jvm-1.7"
    )

    case _ => List(
      "-target:jvm-1.8",
      "-Ywarn-unused"
    )
  }.value ++ Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Ywarn-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture",
    "-Xlint"
  )

scalacOptions in (Compile, doc) ++= baseDirectory.map {
  (bd: File) => Seq[String](
    "-sourcepath", bd.getAbsolutePath,
    "-doc-source-url", "https://github.com/fsaldivars/lambda-notification/tree/master€{FILE_PATH}.scala"
  )
}.value

scalaVersion := "2.12.4"

scmInfo := Some(
  ScmInfo(
    url(s"https://github.com/fsaldivars/$name"),
    s"git@github.com:fsaldivars/$name.git"
  )
)

assemblyJarName in assembly := "lambda-notification.jar"
test in assembly := {}

//Do not applies untils it is Scala
mainClass in assembly := Some("com.fsaldivars.sns.service.SNSNotificationFunction")

version := "1.0"
