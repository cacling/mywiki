package com.ericsson.act

import scala.actors.Actor._

object FasterPerfectNumberFinder {

//  def sumOfFactors(number: Int) = {
//    (0 /: (1 to number)) {
//      (sum, i) =>
//        if (number % i == 0) sum + i
//        else sum
//    }
//  }
//
//  def isPerfect(candidate: Int) = {
//    2 * candidate == sumOfFactors(candidate)
//  }
  
  def sumOfFactorsInRange(lower: Int, upper: Int, number: Int) = {
    (0 /: (lower to upper)) {
      (sum, i) =>
        if (number % i == 0) sum + i
        else sum
    }
  }

  def isPerfectConcurrent(candidate: Int) = {
    val RANGE = 1000000
    val numberOfPartitions = (candidate.toDouble / RANGE).ceil.toInt
    val caller = self
    
    for (i <- 0 until numberOfPartitions) {
      val lower = i * RANGE + 1;
      val upper = candidate min (i + 1) * RANGE;

      actor {
        caller ! sumOfFactorsInRange(lower, upper, candidate)
      }
    }

    val sum = (0 /: (0 until numberOfPartitions)) { (partialSum, i) =>
      receive {
        case sumInRange: Int => partialSum + sumInRange
      }
    }
    
    2 * candidate == sum
  }

  def conutPerfectNumbersInRange(start: Int, end: Int, isPerfectFinder: Int => Boolean) {
    val startTime = System.nanoTime()
    val numberOfPerfectNumbers = (0 /: (start to end)) {
      (count, candidate) =>
        if (isPerfectFinder(candidate)) count + 1
        else count
    }
    val endTime = System.nanoTime()
    println("Found " + numberOfPerfectNumbers + " perfect numbers is given range, took " + (endTime - startTime) / 1000000000.0 + " seconds")
  }

  def main(args: Array[String]) {
//    println(isPerfectConcurrent(33550336))
    val startNumber = 33550300
    val endNumber = 33550400
    //conutPerfectNumbersInRange(startNumber, endNumber, isPerfect)
    conutPerfectNumbersInRange(startNumber, endNumber, isPerfectConcurrent)
  }

}