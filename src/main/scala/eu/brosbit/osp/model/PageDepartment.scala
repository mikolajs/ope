package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

object PageDepartment extends MongoDocumentMeta[PageDepartment] {
  override def collectionName = "pagedepartment"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = PageDepartment(ObjectId.get, "", 99, "", "")
}

case class PageDepartment(var _id: ObjectId, var name: String, var nr: Int,
                          var img: String, var info: String)
  extends MongoDocument[PageDepartment] {
  def meta = PageDepartment
}