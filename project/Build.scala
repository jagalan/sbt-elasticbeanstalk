import sbt._
import Keys._
import bintray.Plugin._
import bintray.Keys._

object Build extends Build {
  lazy val root = Project(
    "sbt-elasticbeanstalk",
    file("."),
    aggregate = Seq(sbtElasticBeanstalkPlugin, sbtElasticBeanstalkCore),
    settings = commonSettings ++ Seq(
      libraryDependencies ++= Seq(
        "com.amazonaws" % "aws-java-sdk" % "1.11.30"
      ),
      publishArtifact := false
    )
  )

  lazy val sbtElasticBeanstalkCore = Project(
    "sbt-elasticbeanstalk-core",
    file("core"),
    settings = commonSettings
  ).settings(
    publishMavenStyle := true,
    sbtPlugin := false,
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-java-sdk" % "1.11.30",
      "org.scalatest" %% "scalatest" % "1.9.2" % "test"
    )
  )

  lazy val sbtElasticBeanstalkPlugin = Project(
    "sbt-elasticbeanstalk-plugin",
    file("plugin"),
    settings = Seq(
    publishMavenStyle := false,
    sbtPlugin := true,
    resolvers += Resolver.url("SQS Ivy", url("http://sqs.github.com/repo"))(Resolver.ivyStylePatterns),
    libraryDependencies <++= scalaVersion { sv => Seq(
      "com.fasterxml.jackson.core" % "jackson-core" % "2.1.1",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.1",
      "net.schmizz" % "sshj" % "0.8.1",
      "org.bouncycastle" % "bcprov-jdk16" % "1.46" 
    ) ++ (if(sv.startsWith("2.10")) Seq (
        "org.scala-lang" % "scala-actors" % sv
      ) else Seq()) 
    }) ++ commonSettings ++ lsimplicitlySettings
  ).dependsOn(sbtElasticBeanstalkCore).aggregate(sbtElasticBeanstalkCore)

  def commonSettings = Defaults.defaultSettings ++
    Seq(
    organization := "com.joescii",
    homepage := Some(url("https://github.com/sqs/sbt-elasticbeanstalk")),
    version := "0.0.8",
    sbtVersion in Global <<= scalaBinaryVersion {
      _ match {
        case "2.10" => "0.13.1"
        case "2.9.2" => "0.12.4"
      }
    },
    scalaVersion in Global := "2.10.3",
    crossScalaVersions := Seq("2.9.2", "2.10.3"),
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    publishArtifact in Test := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false,
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))
    ) ++ bintraySettings 
    
  def bintraySettings = seq(bintrayPublishSettings:_*) ++ Seq(
    repository in bintray := "sbt-plugins",
    bintrayOrganization in bintray := None
  )
  
  def lsimplicitlySettings = {
    import ls.Plugin._
    seq(lsSettings :_*) ++ Seq(
      (LsKeys.tags in LsKeys.lsync) := Seq("amazon", "aws", "elastic beanstalk", "cloud", "web"),
      (description in LsKeys.lsync) := "sbt plugin for deploying WAR files to Amazon Web Services (AWS) Elastic Beanstalk",
      (LsKeys.ghUser in LsKeys.lsync) := Some("sqs"),
      (LsKeys.ghRepo in LsKeys.lsync) := Some("sbt-elasticbeanstalk"),
      (LsKeys.ghBranch in LsKeys.lsync) := Some("master")
    )
  }
}
