package dojo.akkastream.step2

import java.util.UUID.randomUUID

import akka.actor.{ActorLogging, Props}
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}

import scala.concurrent.duration.FiniteDuration
import scala.util.Random

object OrderPublisher {

  def props(delay: FiniteDuration) = Props(new OrderPublisher(delay))
}

class OrderPublisher(delay: FiniteDuration) extends ActorPublisher[Order] with ActorLogging {
  var index: Long = 0L

  // TODO Step2_1 implement the Akka interface
  def receive = {
    case Request(count) =>
      ???

    case Cancel =>
      ???

    case _ =>
  }

  def getNextOrder(): Order = {
    // Random Order
    val order = Order(index, randomUUID().toString, Random.nextLong(), Random.nextDouble(), randomUUID().toString)
    index += 1

    // Artificial delay
    Thread.sleep(delay.toMillis)
    order
  }
}
