package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId


object ContactMail extends MongoDocumentMeta[ContactMail] {
  override def collectionName = "contactmail"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = ContactMail(ObjectId.get, "", "")
}

case class ContactMail(var _id: ObjectId,
                       var description: String, var mailAddress: String)
  extends MongoDocument[ContactMail] {
  def meta = ContactMail
}
