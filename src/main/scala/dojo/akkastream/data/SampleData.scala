package dojo.akkastream.data

import dojo.akkastream.model.Item

import scala.collection.mutable
import scala.util.Random

object SampleData {

  def generateItems(count: Int): Seq[Item] = {
    val buffer: mutable.Buffer[Item] = mutable.Buffer[Item]()
    for (i <- Seq.range(0, count)) {
      buffer += Item(i, Random.nextLong())
    }
    buffer
  }
}
