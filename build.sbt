name := "MovieSimilarities1M"

version := "1.0"

organization := "com.sundogsoftware"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/org.apache.spark/spark-core
  "org.apache.spark" %% "spark-core" % "2.2.0" ,
  "org.apache.spark" %% "spark-streaming" % "2.2.0" ,
  // https://mvnrepository.com/artifact/org.twitter4j/twitter4j-stream
  "org.twitter4j" % "twitter4j-stream" % "4.0.4",
  // https://mvnrepository.com/artifact/org.twitter4j/twitter4j-core
  "org.twitter4j" % "twitter4j-core" % "4.0.4",
  "org.apache.bahir" %% "spark-streaming-twitter" % "2.2.1",
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.3.2",
    // https://mvnrepository.com/artifact/org.apache.spark/spark-sql
  "org.apache.spark" %% "spark-sql" % "2.2.0"


)
