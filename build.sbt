
// The simplest possible sbt build file is just one line:

//scalaVersion := "2.13.12"
//scalaVersion := "3.3.1"
// That is, to create a valid sbt build, all you've got to do is define the
// version of Scala you'd like your project to use.

// ============================================================================

// Lines like the above defining `scalaVersion` are called "settings". Settings
// are key/value pairs. In the case of `scalaVersion`, the key is "scalaVersion"
// and the value is "2.13.12"

// It's possible to define many kinds of settings, such as:

//name := "rs_spacedata"
//organization := "de.htwg.rs_spacedata"
//version := "1.0"

// Note, it's not required for you to define these three settings. These are
// mostly only necessary if you intend to publish your library's binaries on a
// place like Sonatype.


// Want to use a published library in your project?
// You can define other libraries as dependencies in your build like this:

//libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0"
//libraryDependencies ++= Seq(
val dependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.6.17",
  "com.typesafe.akka" %% "akka-stream" % "2.6.17",
  "com.typesafe.akka" %% "akka-http" % "10.2.7",
  "org.asynchttpclient" % "async-http-client" % "2.12.3",
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",
  
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "org.apache.kafka" % "kafka-clients" % "3.6.1",
  "org.json4s" %% "json4s-native" % "3.6.11",
  "io.circe" %% "circe-core" % "0.14.6",
  "io.circe" %% "circe-parser" % "0.14.6",
  "com.typesafe.play" %% "play-json" % "2.9.4",

  "org.apache.spark" %% "spark-sql" % "3.3.4",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.3.4",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.3.4",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.4",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.13.4",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.4",

  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.scalatest" %% "scalatest" % "3.2.10",
  "org.scalatestplus" %% "mockito-5-8" % "3.2.17.0" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.6.17" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.17" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.2.7" % Test,
  "io.github.embeddedkafka" %% "embedded-kafka" % "3.6.1"
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "rs_spacedata",
    version := "1.0",
    scalaVersion :=  "2.13.12",
    // scalaVersion:= "2.12.18",
    organization := "de.htwg.rs_spacedata",
    libraryDependencies ++= dependencies,
    resolvers += "Akka library repository".at("https://repo.akka.io/maven"),
    scalacOptions ++= Seq(
      //"-unchecked",         
      //"-deprecation",      
      //"-feature"
    )
  )

// Here, `libraryDependencies` is a set of dependencies, and by using `+=`,
// we're adding the scala-parser-combinators dependency to the set of dependencies
// that sbt will go and fetch when it starts up.
// Now, in any Scala file, you can import classes, objects, etc., from
// scala-parser-combinators with a regular import.

// TIP: To find the "dependency" that you need to add to the
// `libraryDependencies` set, which in the above example looks like this:

// "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0"

// You can use Scaladex, an index of all known published Scala libraries. There,
// after you find the library you want, you can just copy/paste the dependency
// information that you need into your build file. For example, on the
// scala/scala-parser-combinators Scaladex page,
// https://index.scala-lang.org/scala/scala-parser-combinators, you can copy/paste
// the sbt dependency from the sbt box on the right-hand side of the screen.

// IMPORTANT NOTE: while build files look _kind of_ like regular Scala, it's
// important to note that syntax in *.sbt files doesn't always behave like
// regular Scala. For example, notice in this build file that it's not required
// to put our settings into an enclosing object or class. Always remember that
// sbt is a bit different, semantically, than vanilla Scala.

// ============================================================================

// Most moderately interesting Scala projects don't make use of the very simple
// build file style (called "bare style") used in this build.sbt file. Most
// intermediate Scala projects make use of so-called "multi-project" builds. A
// multi-project build makes it possible to have different folders which sbt can
// be configured differently for. That is, you may wish to have different
// dependencies or different testing frameworks defined for different parts of
// your codebase. Multi-project builds make this possible.

// Here's a quick glimpse of what a multi-project build looks like for this
// build, with only one "subproject" defined, called `root`:

// lazy val root = (project in file(".")).
//   settings(
//     inThisBuild(List(
//       organization := "ch.epfl.scala",
//       scalaVersion := "2.13.12"
//     )),
//     name := "hello-world"
//   )

// To learn more about multi-project builds, head over to the official sbt
// documentation at http://www.scala-sbt.org/documentation.html
