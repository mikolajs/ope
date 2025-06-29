package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

object QuestIndex extends MongoDocumentMeta[QuestIndex] {
  override def collectionName = "questIndex"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create(authorId:Long, subjectId: Long) =
    new QuestIndex(ObjectId.get, authorId, subjectId, 0)
}

case class QuestIndex( _id: ObjectId,  authorId: Long, subjectId: Long,
                       var nr: Int) extends MongoDocument[QuestIndex] {
  def meta = QuestIndex
}

