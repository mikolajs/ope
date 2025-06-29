package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import net.liftweb.json.Formats
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

case class AnswerItem (var q:String, var a: String, var p: Int) {
  import net.liftweb.json.Serialization.write
  import net.liftweb.json.DefaultFormats
  //def json = """ {"q": "%s", "a": "%s" , "p" : "%d" } """.format(q, a.replace("\n", "\\n"), p)
  implicit val formats: DefaultFormats.type = DefaultFormats
  def json: String = write(this)
}

object ExamAnswer extends MongoDocumentMeta[ExamAnswer] {
  override def collectionName = "examanswers"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats: Formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new ExamAnswer(ObjectId.get, ObjectId.get, "", 0, 0L, "", Nil, "", "")
}

case class ExamAnswer(var _id: ObjectId, var exam:ObjectId, var code:String, var max:Int,
                      var authorId: Long,  var authorName : String, var answers: List[AnswerItem],
                      var attach: String, var info: String) extends MongoDocument[ExamAnswer] {
  def meta: ExamAnswer.type = ExamAnswer

}
