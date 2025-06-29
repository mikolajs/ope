package eu.brosbit.osp.model
import _root_.net.liftweb.mongodb._
import net.liftweb.json.Formats
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

case class LessonContent(what: String, id: String, title: String, descript: String) {
  import net.liftweb.json.Serialization.write
  import net.liftweb.json.DefaultFormats
  implicit val formats = DefaultFormats
  def forJSONStr = write(this)
  /* def forJSONStr = "{\"what\":\"" + what + "\",\"id\":\"" + id + "\",\"title\":\"" +
     title + "\",\"descript\":\"" + descript + "\"}"*/
}

// what: headword - h,  quest - q


//case class LessonItem(what: String, id: String, title: String, descript: String)

object LessonCourse extends MongoDocumentMeta[LessonCourse] {
  override def collectionName = "lessons"
  override def formats: Formats = super.formats + new ObjectIdSerializer + new DateSerializer
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier

  def create = new LessonCourse(ObjectId.get, 99, 0L, "", "", "",
    new ObjectId("000000000000000000000000"), Nil)
}

case class LessonCourse(var _id: ObjectId, var nr: Int, var authorId: Long,
                        var chapter: String, var title: String,
                        var descript: String, var courseId: ObjectId, var contents: List[LessonContent])
  extends MongoDocument[LessonCourse] {
  def meta: LessonCourse.type = LessonCourse
  def strJson: String =
    s"""{"_id":"${_id.toString}", "title":"\"${title}\"", "descript":"\"${descript}\"", "chapter":"\"${chapter}\"",
       |"courseId":"$courseId", "contents": [${contents.map(_.forJSONStr).mkString(",")}], "nr":"$nr"}
       |""".stripMargin
  def json: String = {
    import net.liftweb.json.Serialization.write
    import net.liftweb.json.DefaultFormats
    implicit val formats: DefaultFormats.type = DefaultFormats
    write(this)
  }
}