package eu.brosbit.osp.lib

import org.bson.types.ObjectId

object ZeroObjectId {
  def get = new ObjectId("000000000000000000000000")
}
