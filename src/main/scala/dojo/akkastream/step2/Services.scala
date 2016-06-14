package dojo.akkastream.step2

import akka.actor.ActorSystem
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.{DynamoDBMapper, DynamoDBMapperConfig}
import dojo.akkastream.step2.Step2.DynamoTablePrefix

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class Services(system: ActorSystem) {

  implicit val executionContext = system.dispatcher

  val credentials = new DefaultAWSCredentialsProviderChain()
  val dynamoClient = new AmazonDynamoDBClient(credentials)
  dynamoClient.setRegion(Region.getRegion(Regions.EU_WEST_1))
  val dynamoConfig = new DynamoDBMapperConfig.Builder().withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(DynamoTablePrefix)).build
  val dynamoMapper = new DynamoDBMapper(dynamoClient, dynamoConfig)

  def saveOrder(order: Order): Try[Order] = {
    Try {
      dynamoMapper.save(order)
      order
    }
  }

  // TODO Step2_2: method to use wiht mapAsync and parallelism

  protected def doAuditOrder(order: Order): Try[Order] = {
    Try {
      system.log.info(s"Saved Order: $order")
      order
    }
  }

  def auditOrderResultsAsync(order: Order): Future[Try[Order]] = {
    Future {
      doAuditOrder(order)
    }
  }
}
