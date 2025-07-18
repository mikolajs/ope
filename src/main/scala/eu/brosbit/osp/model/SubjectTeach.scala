package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

object SubjectTeach extends MongoDocumentMeta[SubjectTeach] {
  override def collectionName = "subjectsTeach"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new SubjectTeach(ObjectId.get, "", 0L, 0L, 99, 1, Nil)
}

case class SubjectTeach(var _id: ObjectId, var name: String, var id: Long, var authorId: Long,
                        var prior: Int, var lev: Int, var departments: List[String]) extends MongoDocument[SubjectTeach] {
  def meta = SubjectTeach
}