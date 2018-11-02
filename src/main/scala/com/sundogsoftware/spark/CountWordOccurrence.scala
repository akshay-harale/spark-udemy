package com.sundogsoftware.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

object CountWordOccurrence extends App {

  Logger.getLogger("org").setLevel(Level.ERROR)
  val sc = new SparkContext("local[*]", "RatingsCounter")
  val bookData = sc.textFile("./code_files/book.txt")
  bookData.flatMap(_.split(" ")).countByValue().toSeq.sortBy(_._1).foreach(println)
  println("------------------------------------------------")
  println("------------------------------------------------")
  //improvements using regular expression with spark
  bookData.flatMap(_.split("\\W+")).map(_.toLowerCase).countByValue().foreach(println)

  //sorting the result in spark
  bookData.flatMap(_.split("\\W+"))
    .map(x=>(x.toLowerCase,1)).reduceByKey((x,y)=>x+y).map(x=>(x._2,x._1)).sortByKey()
    .collect().foreach(println)


}
