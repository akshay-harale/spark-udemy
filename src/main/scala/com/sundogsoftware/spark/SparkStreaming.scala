package com.sundogsoftware.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import twitter4j.Status

import scala.io.Source


object SparkStreaming extends App {



  def setupLogging: Unit ={
    val value = Logger.getRootLogger()
    value.setLevel(Level.ERROR)
  }
  def setupTwitter(): Unit ={
    val lines = Source.fromFile("./code_files/twitter.txt").getLines()
    for (line<- lines) {
      val fields = line.split(" ")
      if(fields.length == 2){
        System.setProperty("twitter4j.oauth."+fields(0),fields(1))
      }
    }

  }

  setupTwitter()

  val streamingContext = new StreamingContext("local[*]","PopularHashTags",Seconds(1))

  setupLogging

  private val twitterStream: ReceiverInputDStream[Status] = TwitterUtils.createStream(streamingContext,None)

  twitterStream
    .flatMap(s=>s.getText.split(" "))
    .filter(w =>w.startsWith("#"))
    .map(hashtag=>(hashtag,1))
    .reduceByKeyAndWindow((x,y)=>x+y,(x,y)=>x-y,Seconds(300),Seconds(1))
    .transform(rdd=>rdd.sortBy(_._2,false))
    .print()

  streamingContext.checkpoint("/home/synerzip/codebase/spark-udemy/checkpoint")
  streamingContext.start()
  streamingContext.awaitTermination()

}
