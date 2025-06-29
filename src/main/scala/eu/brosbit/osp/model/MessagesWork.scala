package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import eu.brosbit.osp.lib.ZeroObjectId
import net.liftweb.json.{Formats, JsonAST}
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

case class MessageItem (date: String,  a: String, t: Boolean, m: String, l: Boolean) {
  import net.liftweb.json.JsonDSL._
  def toJson :JsonAST.JObject = ("date" -> date)  ~ ("a"->a) ~ ("t"->t) ~ ("m" -> m) ~ ("l" -> l)
}

object  MessagesWork extends MongoDocumentMeta[MessagesWork] {
  override def collectionName = "messageswork"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats:Formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new MessagesWork(ObjectId.get, ZeroObjectId.get, Nil)
}

case class MessagesWork(var _id: ObjectId, var work:ObjectId,
                        var messages: List[MessageItem]) extends MongoDocument[MessagesWork] {
  def meta: MessagesWork.type = MessagesWork

}