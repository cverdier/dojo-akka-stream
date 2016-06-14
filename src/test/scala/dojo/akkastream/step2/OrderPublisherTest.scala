package dojo.akkastream.step2

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, GivenWhenThen, Matchers}

import scala.concurrent.duration._

class OrderPublisherTest extends FunSpec with Matchers with GivenWhenThen with ScalaFutures {

  implicit val actorSystem = ActorSystem("test-system", ConfigFactory.empty())
  implicit val materializer = ActorMaterializer()

  describe("An OrderPublisherActor") {
    it("should send Orders only when requested") {
      // an OrderPublisher Source
      val source = Source.actorPublisher[Order](OrderPublisher.props(50 millis))

      // the stream starts
      val stream = source.runWith(TestSink.probe[Order])

      // request is sent
      val orders = stream.request(2).expectNextN(2)
      orders.map(_.id) should contain theSameElementsAs(List(0, 1))

      // no more messages are sent
      stream.expectNoMsg(1 second)

      // more request is sent
      val moreOrders = stream.request(2).expectNextN(2)
      moreOrders.map(_.id) should contain theSameElementsAs(List(2, 3))
    }
  }

}
