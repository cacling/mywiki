package com.ericsson

import scala.actors._
import scala.actors.Actor._

object SpawnTest {

  class Task extends Actor {
    def act() {
      receive {
        case caller: Actor => caller ! "ok"
      }
    }
  }

  def main(args: Array[String]) {
    val numberOfThreads: Int = 1000000;

    val startTime: Long = System.currentTimeMillis

    val caller = self
    for (i <- 1 to numberOfThreads) {
      val task = new Task()
      task.start()
      task ! caller
    }

    var finishedThreads = 0
    while (finishedThreads < numberOfThreads) {
      receive {
        case "ok" => finishedThreads += 1
      }
    }

    val stopTime: Long = System.currentTimeMillis
    println(numberOfThreads / ((stopTime - startTime) / 1000.0))

  }
}



