package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

///!!! TO DELETE!!!!
object Quiz extends MongoDocumentMeta[Quiz] {
  override def collectionName = "quizzes"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Quiz(ObjectId.get, 0L, "", "", "", 0L, Nil)
}

case class Quiz(var _id: ObjectId, var authorId: Long, var description: String,
                var title: String, var subjectName: String, var subjectId: Long,
                var questions: List[QuestElem]) extends MongoDocument[Quiz] {
  def meta = Quiz
}
