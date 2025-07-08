package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

object MainPageSlide extends MongoDocumentMeta[MainPageSlide] {
  override def collectionName = "MainPageSlide"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = MainPageSlide(ObjectId.get, "", "", "")
}

case class MainPageSlide(var _id: ObjectId,
                         var desc: String, var html: String, var img: String)
  extends MongoDocument[MainPageSlide] {
  def meta = MainPageSlide
}