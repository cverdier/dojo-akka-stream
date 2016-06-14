package dojo.akkastream.step2

import akka.actor.{ActorLogging, Props}
import akka.stream.actor.{ActorSubscriber, MaxInFlightRequestStrategy}
import akka.stream.actor.ActorSubscriberMessage.{OnComplete, OnError, OnNext}

import scala.concurrent.duration.FiniteDuration
import scala.util.Try
import scala.util.control.NonFatal

object OrderResultSubscriber {

  def props(maxInFilght: Int, delay: FiniteDuration) = Props(new OrderResultSubscriber(maxInFilght, delay))
}

class OrderResultSubscriber(maxInFilght: Int, delay: FiniteDuration) extends ActorSubscriber with ActorLogging {

  // TODO Step2_1: MaxInFlightRequestStrategy require you to track inFlight messages
  val requestStrategy = new MaxInFlightRequestStrategy(maxInFilght) {
    override def inFlightInternally: Int = ???
    override def batchSize: Int = maxInFilght
  }

  // TODO Step2_1: implement the Akka interface
  def receive = {
    case OnNext(result: Try[Order]) =>
      ???

    case OnError(err: Exception) =>
      ???

    case OnComplete =>
      ???

    case _ =>
  }

  def orderSuccess(order: Order) = {
    log.info(s"Received success for Order: $order")
  }

  def orderFailure(error: Throwable) = {
    log.warning(s"Received failure for Order : [${error.getClass}] ${error.getMessage}")
  }
}
