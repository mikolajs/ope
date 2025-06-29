package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import net.liftweb.json.Formats
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

object Document extends MongoDocumentMeta[Document] {
  override def collectionName = "documents"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats: Formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Document(ObjectId.get, "", "", "", "", 0L, "", 0L, "", 0)
}

case class Document(var _id: ObjectId, var title: String, var descript: String, var department: String,
                    var content: String, var authorId: Long, var authorName: String, var subjectId: Long,
                    var subjectName: String, var lev: Int) extends MongoDocument[Document] {
  def meta: Document.type = Document
  def json: String = {
    import net.liftweb.json.Serialization.write
    import net.liftweb.json.DefaultFormats
    implicit val formats: DefaultFormats.type = DefaultFormats
    write(this)
  }
}
