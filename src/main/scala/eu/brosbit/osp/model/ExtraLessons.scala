package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import net.liftweb.util.ConnectionIdentifier


object ExtraLessons extends MongoDocumentMeta[ExtraLessons] {
  override def collectionName = "extralessons"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = ExtraLessons(ObjectId.get, "", "", "", "", 0L)
}

case class ExtraLessons(var _id: ObjectId, var title: String, var description: String,
                        var when: String, var teacherName: String, var teacherId: Long) extends MongoDocument[ExtraLessons] {
  def meta = ExtraLessons
}

