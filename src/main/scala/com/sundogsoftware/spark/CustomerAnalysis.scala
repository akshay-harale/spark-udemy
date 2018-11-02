package com.sundogsoftware.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

object CustomerAnalysis extends App{

  Logger.getLogger("org").setLevel(Level.ERROR)
  val sc = new SparkContext("local[*]", "RatingsCounter")
  val customerOrders = sc.textFile("./code_files/customer-orders.csv")
  customerOrders.map(f=>{
    val row = f.split(",")
    val customerId=row(0).toInt
    val amountSpent = row(2).toFloat
    (customerId,amountSpent)
  })
    .reduceByKey((x,y)=>x+y)
    .sortBy(_._2)
    .collect().foreach(println)

}
