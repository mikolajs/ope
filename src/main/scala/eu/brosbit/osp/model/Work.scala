package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import eu.brosbit.osp.lib.ZeroObjectId
import net.liftweb.util.ConnectionIdentifier

object Work extends MongoDocumentMeta[Work] {
  override def collectionName = "Works"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Work(ObjectId.get, ZeroObjectId.get, ZeroObjectId.get, 0L,
    "", "", "", "", "", 0L, "", 0L, 0L)
}

case class Work(var _id: ObjectId, var lessonId: ObjectId, var courseId: ObjectId, var teacherId: Long,
                var theme: String, var description: String, var lessonTitle: String, var groupId: String,
                var groupName:String, var subjectId: Long, var subjectName: String, var start: Long,
                var lastNews: Long) extends MongoDocument[Work] {
  def meta = Work
}