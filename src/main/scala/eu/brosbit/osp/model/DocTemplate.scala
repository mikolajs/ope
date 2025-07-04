package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import net.liftweb.util.ConnectionIdentifier


object DocTemplate extends MongoDocumentMeta[DocTemplate] {
  override def collectionName = "DocTemplate"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = DocTemplate(ObjectId.get, false, "", "", "")
}

case class DocTemplate(var _id: ObjectId, var tab: Boolean, var title: String,
                       var comment: String, var template: String)
  extends MongoDocument[DocTemplate] {
  def meta = DocTemplate
}