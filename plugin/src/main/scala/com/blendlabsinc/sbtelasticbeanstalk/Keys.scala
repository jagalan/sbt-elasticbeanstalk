package com.blendlabsinc.sbtelasticbeanstalk

import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient
import com.amazonaws.services.elasticbeanstalk.model._
import java.io.File
import sbt.{ SettingKey, TaskKey }

case class Deployment(
  appName: String,
  envBaseName: String,
  templateName: String,
  cname: String,
  environmentVariables: Map[String, String] = Map()
)

object ElasticBeanstalkKeys {
  val ebS3BucketName = SettingKey[String]("ebS3BucketName", "S3 bucket which should contain uploaded WAR files")
  val ebDeployments = SettingKey[Seq[Deployment]]("eb-deployments", "List of Elastic Beanstalk deployment targets")

  val ebEnvironmentNameSuffix = SettingKey[Function1[String,String]]("eb-environment-name-suffix", "Function that returns an environment name suffixed with a unique string")

  val ebRegion = SettingKey[String]("ebRegion", "Elastic Beanstalk region (e.g., us-west-1)")

  val ebDeploy = TaskKey[Unit]("eb-deploy", "Deploy the application WAR to Elastic Beanstalk")
  val ebWait = TaskKey[Unit]("eb-wait", "Wait for all project environments to be Ready and Green")

  val ebSetUpEnvForAppVersion = TaskKey[Map[Deployment,EnvironmentDescription]]("eb-setup-env-for-app-version", "Sets up a new environment for the WAR file.")

  val ebConfigPull = TaskKey[List[File]]("eb-config-pull", "Downloads existing configurations for all project environments")
  val ebConfigPush = TaskKey[Unit]("eb-config-push", "Updates configurations for all project environments using local configs (that were pulled with eb-config-pull)")

  val ebParentEnvironments = TaskKey[Map[Deployment,Option[EnvironmentDescription]]]("eb-parent-environments", "Returns existing environments corresponding to all project environments. If a project environment has a CNAME set, then it attempts to find the existing environment with that CNAME. If no CNAME is set, it finds the existing environment with the same environment name.")

  val ebTargetEnvironments = TaskKey[Map[Deployment,EnvironmentDescription]]("eb-target-environments")

  val ebExistingEnvironments = TaskKey[List[EnvironmentDescription]]("eb-existing-environments", "Describes all existing project environments (i.e., that are on Elastic Beanstalk)")

  val ebCleanEnvironments = TaskKey[Unit]("eb-clean", "Terminates all old and unused environments")

  val ebUploadSourceBundle = TaskKey[S3Location]("eb-upload-source-bundle", "Uploads the WAR source bundle to S3")

  val ebConfigDirectory = SettingKey[File]("eb-config-directory", "Where EB configs are pulled to and pushed from")

  val ebApiDescribeApplications = TaskKey[List[ApplicationDescription]]("eb-api-describe-applications", "Returns the descriptions of existing applications")
  val ebApiDescribeEnvironments = TaskKey[List[EnvironmentDescription]]("eb-api-describe-environments", "Returns descriptions for existing environments")

  val ebClient = TaskKey[AWSElasticBeanstalkClient]("eb-client")

  // Debug
  val ebRequireJava6 = SettingKey[Boolean]("eb-require-java6", "Require Java6 to deploy WAR (as of Dec 2012, Java7 is incompatible with EB)")
}
