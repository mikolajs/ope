package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import net.liftweb.json.Formats
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

case class QuestElem(q:ObjectId, p:Int)

object Exam extends MongoDocumentMeta[Exam] {
  override def collectionName = "Exams"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats: Formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new Exam(ObjectId.get, 0L, "", 0L, "", "", "",  Nil, Nil, 0L, 0L, true, false)
}

case class Exam(var _id: ObjectId, var authorId: Long,  var description: String,
                var subjectId: Long, var subjectName: String,
                var groupId: String, var groupName:String, var quizzes: List[List[QuestElem]],
                var keys: List[String], var start: Long, var end: Long, var multi: Boolean,
                var attach: Boolean) extends MongoDocument[Exam] {
  def meta: Exam.type = Exam
}