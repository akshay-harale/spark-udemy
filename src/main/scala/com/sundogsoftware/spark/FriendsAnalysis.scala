package com.sundogsoftware.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object FriendsAnalysis extends App {

  def parseLineToGetAgeAndFriends(line: String) = {
    val lines = line.split(",")
    val age = lines(2).toInt
    val noOfFriends = lines(3).toInt
    (age, noOfFriends)
  }

  Logger.getLogger("org").setLevel(Level.ERROR)
  val sc = new SparkContext("local[*]", "RatingsCounter")
  val friendsData = sc.textFile("./code_files/fakefriends.csv")
  val rdd: RDD[(Int, Int)] = friendsData.map(parseLineToGetAgeAndFriends)
  private val totalByAge: RDD[(Int, (Int, Int))] = rdd.mapValues(f => (f, 1)).reduceByKey((x, y) => ((x._1 + y._1), x._2 + y._2))
  private val averageAge: RDD[(Int, Int)] = totalByAge.mapValues(x => x._1 / x._2)
  averageAge.collect().sortBy(_._2).foreach(println)

  friendsData.map(f=>{
    val value = f.split(",")
    val names = value(1)
    val friends = value(3)
    (names,friends.toInt)
  }).mapValues(x=>(x,1)).reduceByKey((x,y)=>(x._1+y._1,x._2+y._2))
    .mapValues(x=>x._1/x._2)
    .collect().sortBy(_._2).foreach(println)



}
