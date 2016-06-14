package dojo.akkastream.step2

import java.math.BigInteger

import akka.actor.{ActorLogging, Props}
import akka.stream.actor.ActorSubscriberMessage.{OnComplete, OnError, OnNext}
import akka.stream.actor.{ActorSubscriber, WatermarkRequestStrategy}

import scala.concurrent.duration.FiniteDuration

object OrderResultSubscriber {

  def props(delay: FiniteDuration) = Props(new OrderResultSubscriber(delay))
}

class OrderResultSubscriber(delay: FiniteDuration) extends ActorSubscriber with ActorLogging {
  val requestStrategy = WatermarkRequestStrategy(50)

  def receive = {
    case OnNext(number: BigInteger) =>
      log.info(s"Received Number: $number")
      Thread.sleep(delay.toMillis)

    case OnError(err: Exception) =>
      log.error(err, "Receieved Exception in Stream -- Stopping")
      context.stop(self)

    case OnComplete =>
      log.info("Stream Completed! -- Stopping")
      context.stop(self)

    case _ =>
  }
}
