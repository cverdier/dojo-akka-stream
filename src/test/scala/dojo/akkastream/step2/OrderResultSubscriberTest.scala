package dojo.akkastream.step2

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink}
import akka.stream.testkit.scaladsl.TestSource
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, GivenWhenThen, Matchers}

import scala.util.{Failure, Success, Try}
import scala.concurrent.duration._

class OrderResultSubscriberTest extends FunSpec with Matchers with GivenWhenThen with ScalaFutures {

  implicit val actorSystem = ActorSystem("test-system", ConfigFactory.empty())
  implicit val materializer = ActorMaterializer()

  describe("An OrderResultSubscriber") {
    it("should send initial request, and send more when order results are processed") {
      // some order results
      val orders = List(Success(randomOrder(0)), Success(randomOrder(1)), Success(randomOrder(2)), Success(randomOrder(3)))

      // an OrderResultSubscriberSink
      val sink = Sink.actorSubscriber[Try[Order]](OrderResultSubscriber.props(4, 50 millis))

      // the stream starts
      val stream = TestSource.probe[Try[Order]]
        .toMat(sink)(Keep.left)
        .run()

      // request is sent according to maxInFlight
      stream.expectRequest() should be (4L)
      stream.expectNoMsg(1 second)

      // messages are sent back matching request
      stream.unsafeSendNext(orders(0))
      stream.unsafeSendNext(orders(1))
      stream.unsafeSendNext(orders(2))
      stream.unsafeSendNext(orders(3))

      // more request is sent
      stream.expectRequest() should be (4L)
      stream.expectNoMsg(1 second)
    }

    it("should send request even when failed results arrive") {
      // some order results
      val orders = List(Failure(new Exception("0")), Success(randomOrder(1)), Failure(new Exception("2")), Failure(new Exception("3")))

      // an OrderResultSubscriberSink
      val sink = Sink.actorSubscriber[Try[Order]](OrderResultSubscriber.props(4, 50 millis))

      // the stream starts
      val stream = TestSource.probe[Try[Order]]
        .toMat(sink)(Keep.left)
        .run()

      // request is sent according to maxInFlight
      stream.expectRequest() should be (4L)
      stream.expectNoMsg(1 second)

      // messages are sent back matching request, including failures
      stream.unsafeSendNext(orders(0))
      stream.unsafeSendNext(orders(1))
      stream.unsafeSendNext(orders(2))
      stream.unsafeSendNext(orders(3))

      // more request is sent
      stream.expectRequest() should be (4L)
      stream.expectNoMsg(1 second)
    }

  }

  def randomOrder(index: Int) = Order(index, s"client-$index", index, index.toDouble, s"reason-index")
}
