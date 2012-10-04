package com.ericsson.concurrent

import scala.actors._
import scala.actors.Actor._
import java.util.Random

object HambergShopBlock {

  class Hamberg(id: Int, cookerid: String) {
    override def toString(): String = "#" + id + " by " + cookerid
  }

  class Waiter extends Actor {
    val consumerPIDs = new scala.collection.mutable.HashSet[Actor]
    def act() { 
      loop {
        receive {
          case (consumerPID: Actor, "noHamberg") =>
            println("Waiter: Sorry, Sir, there is no hambergs left, please wait!")
            consumerPID ! (self, "pleaseWait")
            consumerPIDs.add(consumerPID)
          case (cookerPID: Actor, "hasHamberg") =>
            println("Waiter: Hi! Customers, we got some new Hambergs!")
            consumerPIDs.foreach(pid => pid ! (self, "hasHamberg"))
            consumerPIDs.clear()
          case other => println(self + " has received unexpected message " + other)
        }
      }
    }
  }

  class HambergFifo extends Actor {
    val hambergs = new scala.collection.mutable.Stack[Hamberg]
    val maxSize = 10
    def act() {
      loop {
        receive {
          case (consumerPID: Actor, "getHamberg") if !hambergs.isEmpty =>
            println("There are still " + hambergs.length + " hambergs left")
            consumerPID ! (self, "heresHamberg", hambergs.pop)
          case (consumerPID: Actor, "getHamberg") =>
            println("There are no hambergs left")
            consumerPID ! (self, "noHamberg")
          case (cookerPID: Actor, "ishambergFifoFull") if hambergs.length >= maxSize => cookerPID ! (self, "hambergFifoisFull")
          case (cookerPID: Actor, "ishambergFifoFull") => cookerPID ! (self, "hambergFifoisNotFull")
          case (cookerPID: Actor, "putHamberg", hamberg: Hamberg) if hambergs.length < maxSize =>
            hambergs.push(hamberg)
            println("There are " + hambergs.length + " Hambergs in all")
          case (cookerPID: Actor, "putHamberg", hamberg: Hamberg) =>
            println("There are " + hambergs.length + " Hambergs in all")
          case other => println(self + " has received unexpected message " + other)
        }
      }

    }
  }

  class Cooker(name: String, hambergFifoPID: Actor, waiterPID: Actor) extends Actor {
    var madeCount = 0;
    def act() {
      loop {
        hambergFifoPID ! (self, "ishambergFifoFull")
        receive {
          case (hambergFifo: Actor, "hambergFifoisFull") =>
            println(name +
              ", Hamberg Pool is Full, Stop making hamberg")
          case (hambergFifo: Actor, "hambergFifoisNotFull") =>
            madeCount += 1
            hambergFifoPID ! (self, "putHamberg", new Hamberg(madeCount, name))
            waiterPID ! (self, "hasHamberg")
          case other => println(self + " has received unexpected message " + other)
        }
        Thread.sleep(3000);
      }
    }
  }

  class Customer(name: String, hambergFifoPID: Actor, waiterPID: Actor) extends Actor {
    val r = new Random();

    def act() {
      loop {
        hambergFifoPID ! (self, "getHamberg")
        receive {
          case (waiter: Actor, "heresHamberg", hamberg: Hamberg) => eatHamberg(hamberg)
          case (waiter: Actor, "pleaseWait") =>
            println(name + ": OK, Waiting for new hambergs")
            receive {
              case (waiter: Actor, "hasHamberg") => hambergFifoPID ! (self, "getHamberg")
            }
          case (hambergFifo: Actor, "noHamberg") =>
            println(name + ": OH MY GOD!!!! No hambergs left, Waiter![Ring the bell besides the hamberg pool]")
            waiterPID ! (self, "noHamberg")
          case other => println(self + " has received unexpected message " + other)
        }
      }
    }

    def eatHamberg(hamberg: Hamberg) {
      println(name + ": I Got Hamberg " + hamberg);
      val sleeptime = Math.abs(r.nextInt(1000)) * 5
      println(name + ": I'm eating the hamberg for " + sleeptime + " milliseconds");
      Thread.sleep(sleeptime)
    }
  }

  def main(args: Array[String]) {
    val hambergFifo = new HambergFifo();
    hambergFifo.start();
    val waiter = new Waiter();
    waiter.start();
    val t1 = new Customer("Customer 1", hambergFifo, waiter)
    val t2 = new Customer("Customer 2", hambergFifo, waiter)
    val t3 = new Customer("Customer 3", hambergFifo, waiter) 
    val t4 = new Cooker("Cooker 1", hambergFifo, waiter)
    val t5 = new Cooker("Cooker 2", hambergFifo, waiter)
    val t6 = new Cooker("Cooker 3", hambergFifo, waiter)
    t4.start()
    t5.start()
    t6.start()
    Thread.sleep(1000)
    t1.start()
    t2.start()
    t3.start()
  }
}