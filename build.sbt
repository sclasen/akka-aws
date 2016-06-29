name := "akka-aws"

lazy val commonSettings = Seq(
 organization := "com.sclasen",
 version := "0.3.9-SNAPSHOT",
 scalaVersion := "2.11.2",
 crossScalaVersions := Seq("2.10.4", "2.11.2")
)

scalacOptions ++= Seq("-feature", "-deprecation", "-language:implicitConversions", "-language:postfixOps")

resolvers ++= Seq(
  "TypesafeMaven" at "http://repo.typesafe.com/typesafe/maven-releases",
  "whydoineedthis" at "http://repo.typesafe.com/typesafe/releases",
  "oss" at "https://oss.sonatype.org/"
)

def deps = Seq(aws, akka, scalaTest, akka_testkit, akka_http, akka_http_core, akka_http_spray)

val akkaVersion = "2.4.7"

val aws = "com.amazonaws" % "aws-java-sdk" % "1.8.9.1" % "compile"
val akka = "com.typesafe.akka" %% "akka-actor" % akkaVersion % "compile"
val akka_http = "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion
val akka_http_core = "com.typesafe.akka" %% "akka-http-core" % akkaVersion
val akka_http_spray = "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion
val akka_testkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
val scalaTest   = "org.scalatest"     %% "scalatest"   % "2.2.1" % "test"

libraryDependencies ++= deps

lazy val root = project
  .in(file("."))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= deps)
  .aggregate(akka_aws, akka_dynamodb, akka_kinesis, akka_sqs, akka_route53, akka_s3)

lazy val akka_aws = project
  .in(file("akka-aws"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= deps)

lazy val akka_dynamodb = project
  .in(file("akka-dynamodb"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= deps)
  .dependsOn(akka_aws)

lazy val akka_sqs = project
  .in(file("akka-sqs"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= deps)
  .dependsOn(akka_aws)

lazy val akka_kinesis = project
  .in(file("akka-kinesis"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= deps)
  .dependsOn(akka_aws)

lazy val akka_route53 = project
  .in(file("akka-route53"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= deps)
  .dependsOn(akka_aws)

lazy val akka_s3 = project
  .in(file("akka-s3"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= deps)
  .dependsOn(akka_aws)

// If we want on maven central, we need to be in maven style.
publishMavenStyle := true

publishArtifact in Test := false

// The Nexus repo we're publishing to.
publishTo in ThisBuild <<= version {
  (v: String) =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

// Maven central cannot allow other repos.  We're ok here because the artifacts we
// we use externally are *optional* dependencies.
pomIncludeRepository := {
  x => false
}

// Maven central wants some extra metadata to keep things 'clean'.
pomExtra :=
  <url>http://github.com/sclasen/akka-aws</url>
    <licenses>
      <license>
        <name>The Apache Software License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:sclasen/akka-aws.git</url>
      <connection>scm:git:git@github.com:sclasen/akka-aws.git</connection>
    </scm>
    <developers>
      <developer>
        <id>sclasen</id>
        <name>Scott Clasen</name>
        <url>http://github.com/sclasen</url>
      </developer>
    </developers>

publishArtifact in Compile := false

fork in Test := true
