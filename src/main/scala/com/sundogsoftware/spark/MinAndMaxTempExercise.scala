package com.sundogsoftware.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

import scala.math._
object MinAndMaxTempExercise extends App{
  Logger.getLogger("org").setLevel(Level.ERROR)
  val sc = new SparkContext("local[*]", "RatingsCounter")
  val tempData = sc.textFile("./code_files/1800.csv")
    .map(x=>{
      val fields = x.split(",")
      val stationId = fields(0)
      val entryType = fields(2)
      val temperature =fields(3).toFloat * 0.1F * (9.0f/5.0f) + 32 // conver the temp into fahrenheit
      (stationId,entryType,temperature)
    })

  private val minTemp = tempData.filter(_._2 == "TMIN")
    .map(f=>(f._1,f._3)).reduceByKey((x,y)=>min(x,y)).collect()

  for(tmp<-minTemp){
    val station=tmp._1
    val temp=tmp._2
    val formatedTemp=f"$temp%.2f F"
    println(s"$station minimum temperature is $formatedTemp")
  }

  private val maxTemp = tempData.filter(_._2 == "TMAX")
    .map(f=>(f._1,f._3)).reduceByKey((x,y)=>max(x,y)).collect()

  for(tmp<-maxTemp){
    val station=tmp._1
    val temp=tmp._2
    val formatedTemp=f"$temp%.2f F"
    println(s"$station maximum temperature is $formatedTemp")
  }


}
