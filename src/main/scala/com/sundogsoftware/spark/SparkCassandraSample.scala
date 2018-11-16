package com.sundogsoftware.spark

import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object SparkCassandraSample extends App {
  val cassandraHost = "localhost:9092"

  Logger.getLogger("org").setLevel(Level.ERROR)

  // Create a SparkContext using every core of the local machine
  val sc = new SparkContext("local[*]", "SparkCassandra")
  val spark = SparkSession
    .builder()
    .appName("Spark SQL basic example")
    .config("spark.some.config.option", "some-value")
    .getOrCreate()

  val sparkConf = spark.sparkContext.getConf
  val createKeyspace = "CREATE KEYSPACE IF NOT EXISTS test WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };"
  val createTable = "CREATE TABLE IF NOT EXISTS test.kv(key text PRIMARY KEY, value int);"
  import com.datastax.spark.connector.cql.CassandraConnector
  val cassandraConnector = CassandraConnector(sparkConf )
  cassandraConnector.withSessionDo { session =>
    session.execute(createKeyspace)
    session.execute(createTable)
  }

  CassandraConnector(sparkConf).withSessionDo{ session =>
    for (i <- 0 until 100) {
      val insertStatement = s"INSERT INTO test.kv(key, value) VALUES ('key${i}', $i);"
      session.execute(insertStatement)
    }
  }
  private val fields = Seq(StructField("key", StringType, true),StructField("value", IntegerType, true))
  val schema =
    StructType(
      fields)
  import com.datastax.spark.connector._
  val rdd = sc.cassandraTable("test", "kv")
  private val frame: DataFrame = spark.createDataFrame(
    rdd.map(r => Row.fromSeq(r.columnValues)), schema
  )
  frame.show()

}
