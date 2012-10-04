package com.ericsson.concurrent

import scala.actors._
import scala.actors.Actor._

object NotifyTest {

  class NotifyThread(val name: String, val pids:Actor*) extends Actor {
    def act() {
      Thread.sleep(3000)
      pids.foreach(pid => pid ! "go")
    }
  }

  class WaitThread(val name: String) extends Actor {
    def act() {
      println(name + " begin waiting!")
      var waitTime = System.currentTimeMillis;
      receive {
        case "go" => waitTime = System.currentTimeMillis - waitTime
			      println("wait time :" + waitTime)
			      println(name + " end waiting!")
        case  _ => println("unexpected")
      }
      
    }
  }

  def main(args: Array[String]) {
    println("Main Thread Run!");
    val waitThread01 = new WaitThread("waiter01")
    val waitThread02 = new WaitThread("waiter02")
    val waitThread03 = new WaitThread("waiter03")
    val notifyThread = new NotifyThread("notify01",waitThread01, waitThread02, waitThread03)
    waitThread01.start()
    waitThread02.start()
    waitThread03.start()
    notifyThread.start()

  }

}