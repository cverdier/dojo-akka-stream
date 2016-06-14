package dojo.akkastream.step2

import com.amazonaws.services.dynamodbv2.datamodeling.{DynamoDBAttribute, DynamoDBHashKey, DynamoDBRangeKey, DynamoDBTable}

@DynamoDBTable(tableName = "Order")
case class Order
(
  id: Long,
  client: String,
  product: Long,
  amount: Double,
  reason: String
) {
  @DynamoDBRangeKey(attributeName = "Id")
  def getId(): java.lang.Long = id

  @DynamoDBHashKey(attributeName = "Client")
  def getClient(): java.lang.String = client

  @DynamoDBAttribute(attributeName = "Product")
  def getProduct(): java.lang.Long = product

  @DynamoDBAttribute(attributeName = "Amount")
  def getAmount(): java.lang.Double = amount

  @DynamoDBAttribute(attributeName = "Reason")
  def getReason(): java.lang.String = reason
}
