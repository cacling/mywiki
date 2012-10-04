package com.ericsson.act
import scala.actors.Actor
import java.util.concurrent.CountDownLatch
import scala.util.Random
import scala.actors.TIMEOUT

object MicroBenchmark {
  val processorCount = Runtime.getRuntime.availableProcessors + 1
  val executeTimes = processorCount * 100

  def main(args: Array[String]): Unit = {
    val beginTime = System.currentTimeMillis
    val latchCount = executeTimes * processorCount
    var latch = new CountDownLatch(latchCount)
    var i = 0
    while (i < processorCount) {
      new Thread(new NotifyProcessorTask(latch)).start
      i += 1
    }
    latch.await()
    val endTime = System.currentTimeMillis;
    println("execute time: " + (endTime - beginTime) + "ms")
    exit
  }
}

class NotifyProcessorTask(latch: CountDownLatch) extends Runnable {
  val executeTimes = 1000
  val random = new Random
  val actor = new Processor

  def run() {
    var i = 0
    actor.start
    while (i < executeTimes) {
      actor ! TaskMessage.TaskExecuted(latch, 0)
      i += 1
      Thread.sleep(random.nextInt(5))
    }
    latch.countDown()
  }
}

abstract sealed class TaskMessage

object TaskMessage {
  case class TaskExecuted(latch: CountDownLatch, counter: Int) extends TaskMessage
}

class Processor extends Actor {
  var random = new Random

  def act() {
    loop {
      react {
        case TaskMessage.TaskExecuted(latch, counter) => handle(latch, counter)
      }
    }
  }

  def handle(latch: CountDownLatch, counter: Int) {
    val list = new Array[String](1000)
    var i = 0
    for (i <- 0 to 999) {
      list(i) = i.toString
    }
    var selfCounter = counter;
    if (selfCounter < 99) {
      selfCounter += 1
      new Sleeper(random.nextInt(2), this, selfCounter, latch).start
    } else {
      latch.countDown()
    }
  }

}

class Sleeper(time: Long, origin: Actor, counter: Int, latch: CountDownLatch) extends Actor {
  def act() {
    reactWithin(time) {
      case TIMEOUT =>
        origin ! TaskMessage.TaskExecuted(latch, counter)
    }
  }
}

