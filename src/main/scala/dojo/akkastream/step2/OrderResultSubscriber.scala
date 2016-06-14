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
  var currentInFlight: Int = 0
  val requestStrategy = new MaxInFlightRequestStrategy(maxInFilght) {
    override def inFlightInternally: Int = currentInFlight
    override def batchSize: Int = maxInFilght
  }

  def receive = {
    case OnNext(result: Try[Order]) =>
      currentInFlight += 1
      result
        .map { order => orderSuccess(order) }
        .recover { case NonFatal(error) => orderFailure(error) }

      // Artificial delay
      Thread.sleep(delay.toMillis)
      currentInFlight -= 1

    case OnError(err: Exception) =>
      log.error(err, "Receieved Exception in Stream -- Stopping")
      context.stop(self)

    case OnComplete =>
      log.info("Stream Completed! -- Stopping")
      context.stop(self)

    case _ =>
  }

  def orderSuccess(order: Order) = {
    log.info(s"Received success for Order: $order")
  }

  def orderFailure(error: Throwable) = {
    log.warning(s"Received failure for Order : [${error.getClass}] ${error.getMessage}")
  }
}
