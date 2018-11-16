package com.sundogsoftware.spark

import java.nio.charset.CodingErrorAction

import com.sundogsoftware.spark.MostPopularMovie.sc
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast

import scala.io.{Codec, Source}

object MostPopularSuperhero extends App{

  private def idNamePair(x:String): Option[(Int,String)]  = {
    val fields = x.split("\"")
    if (fields.length > 1) {
      Some(fields(0).trim.toInt, fields(1))
    } else {
      None
    }
  }

  def getIdNamesMap() ={
    var heroNames:Map[Int,String]=Map()
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
    Source.fromFile("./code_files/Marvel-names.txt").getLines()
      //.map(idNamePair)
      .foreach(x=> {
      val fields = x.split("\"")
      if(fields.length>1)
        heroNames += (fields(0).trim.toInt -> fields(1))
    })
    heroNames
  }

  Logger.getLogger("org").setLevel(Level.ERROR)
  val sc = new SparkContext("local[*]", "SuperheroRating")
  private val appearancesCount = sc.textFile("./code_files/Marvel-graph.txt").map(x => {
    val fields = x.split("\\s+")
    val heroId = fields(0).toInt
    val occurrences = fields.length - 1
    (heroId, occurrences)
  }).reduceByKey((x, y) => x + y)
    .map(x => (x._2, x._1))

  val maxCount = appearancesCount.max()
  private val namesDictionary  = sc.broadcast(getIdNamesMap())
  val heroNames = sc.textFile("./code_files/Marvel-names.txt")
    .flatMap(idNamePair)
  private val mostPopularName: String = heroNames.lookup(maxCount._2)(0)
  println(s"MostPopular hero name is $mostPopularName with ${maxCount._1} appearances")
  appearancesCount.sortByKey(false).map(f=>(namesDictionary.value(f._2),f._1))
    .take(20)
    .foreach(println)

}
