package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

object Video extends MongoDocumentMeta[Video] {
  override def collectionName = "videos"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Video(ObjectId.get, "", "", "", 1, 0L, 0L, "", "", "", "", false)
}

case class Video(var _id: ObjectId, var link: String, var oldPath: String, var mime: String,
                 var lev: Int, var authorId: Long, var subjectId: Long,
                 var subjectName: String, var title: String, var department: String,
                 var descript: String, var onServer: Boolean) extends MongoDocument[Video] {
  def meta: Video.type = Video
  def json: String = {
    import net.liftweb.json.Serialization.write
    import net.liftweb.json.DefaultFormats
    implicit val formats: DefaultFormats.type = DefaultFormats
    write(this)
  }
}
