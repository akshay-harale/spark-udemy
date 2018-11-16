package com.sundogsoftware.spark

import java.nio.charset.CodingErrorAction

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast

import scala.io.{Codec, Source}

object MostPopularMovie extends App {

  def loadMovieNames={
    var movieNames:Map[Int,String]=Map()
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    Source.fromFile("../ml-100k-dataset/u.item").getLines()
      .foreach(f=>{
        val fields = f.split("\\|")
        if(fields.length>1)
          movieNames += (fields(0).toInt -> fields(1))
      })
    movieNames
  }

  private val movieNames = loadMovieNames

  Logger.getLogger("org").setLevel(Level.ERROR)
  val sc = new SparkContext("local[*]", "MovieRating")
  val movieRatingsData = sc.textFile("../ml-100k-dataset/u.data").map(_.split("\t"))
  //userId-0,movieId-1,Rating-2,timestamp-3

  private val movieDict: Broadcast[Map[Int, String]] = sc.broadcast(movieNames)

  movieRatingsData
    .map(x=>(x(1),1))
    .reduceByKey((x,y)=>x+y)
    .sortBy(_._2)
    .map(x=>(movieDict.value(x._1.toInt),x._2))
    .collect()
    .foreach(println)

  //broadcast variable to resolve movie names
}
