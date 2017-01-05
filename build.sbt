// Your sbt build file. Guides on how to write one can be found at
// http://www.scala-sbt.org/0.13/docs/index.html

organization := "databricks"

name := "spark-corenlp"

version := "0.3.0-SNAPSHOT"

crossScalaVersions := Seq("2.11.8", "2.10.6")

initialize := {
  val _ = initialize.value
  val required = VersionNumber("1.8")
  val current = VersionNumber(sys.props("java.specification.version"))
  assert(VersionNumber.Strict.isCompatible(current, required), s"Java $required required.")
}

scalaVersion := "2.11.8"

sparkVersion := "2.1.0"

// change the value below to change the directory where your zip artifact will be created
spDistDirectory := target.value

spAppendScalaVersion := true

credentials += Credentials(Path.userHome / ".ivy2" / ".sbtcredentials")

sparkComponents += "sql"

// add any sparkPackageDependencies using sparkPackageDependencies.
// e.g. sparkPackageDependencies += "databricks/spark-avro:0.1"
spName := "databricks/spark-corenlp"

licenses := Seq("GPL-3.0" -> url("http://opensource.org/licenses/GPL-3.0"))

resolvers ++= Seq(
  "Jieba Analysis" at "https://mvnrepository.com/artifact/com.huaban/jieba-analysis",
  Resolver.sonatypeRepo("public")
)
resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "com.huaban" % "jieba-analysis" % "1.0.2",
  "com.google.protobuf" % "protobuf-java" % "2.6.1",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models-chinese",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" % "test" classifier "models",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" % "test" classifier "models-chinese",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)
