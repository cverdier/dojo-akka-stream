package dojo.akkastream.step2

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object Step2 {

  val DynamoTablePrefix = "Dojo.CVE."
}

object Step2_1 extends App {

  implicit val system = ActorSystem("step2_1", ConfigFactory.empty())
  implicit val materializer: Materializer = ActorMaterializer()

  val source = Source.actorPublisher[Order](OrderPublisher.props(100 millis))
  val sink = Sink.actorSubscriber[Try[Order]](OrderResultSubscriber.props(16, 100 millis))

  val services = new Services(system)

  system.log.info("Running Stream")
  source
    .map(services.saveOrder)
    .runWith(sink)
}

object Step2_2 extends App {

  implicit val system = ActorSystem("step2_2", ConfigFactory.empty())
  implicit val materializer: Materializer = ActorMaterializer()

  val source = Source.actorPublisher[Order](OrderPublisher.props(50 millis))
  val sink = Sink.actorSubscriber[Try[Order]](OrderResultSubscriber.props(16, 50 millis))

  val services = new Services(system)
  val parallelism = 4

  system.log.info("Running Stream")
  source
    .mapAsync(parallelism)(services.saveOrderAsync)
    .runWith(sink)
}

object Step2_3 extends App {

  implicit val system = ActorSystem("step2_3", ConfigFactory.empty())
  implicit val materializer: Materializer = ActorMaterializer()

  val source = Source.actorPublisher[Order](OrderPublisher.props(50 millis))
  val sink = Sink.actorSubscriber[Try[Order]](OrderResultSubscriber.props(16, 50 millis))

  val services = new Services(system)
  val parallelism = 4

  system.log.info("Running Stream")
  source
    .mapAsync(parallelism)(services.saveOrderAsync)
    .mapAsync(parallelism) {
      case Success(order) => services.auditOrderResultsAsync(order)
      case Failure(error) => Future.successful(Failure(error))
    }
    .runWith(sink)
}