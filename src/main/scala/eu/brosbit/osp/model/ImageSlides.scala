package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import net.liftweb.json.Formats
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

object ImageSlides extends MongoDocumentMeta[ImageSlides] {
  override def collectionName = "imageSlides"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats: Formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new ImageSlides(ObjectId.get, "", "", 0L,  0)
}

case class ImageSlides(val _id: ObjectId, var src: String, var code:String,
                       var author: Long, var order: Int)
  extends MongoDocument[ImageSlides] {
  def meta: ImageSlides.type = ImageSlides
}

