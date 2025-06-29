package eu.brosbit.osp.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import net.liftweb.util.ConnectionIdentifier

object PSO extends MongoDocumentMeta[PSO] {
  override def collectionName = "PSO"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = PSO(ObjectId.get, Nil, "", "", "", 0L)
}

case class PSO(var _id: ObjectId, var classes:List[String],
               var subjectStr:String, var urlLink:String,
               var teacherName:String, var teacherId:Long)
  extends MongoDocument[PSO] {
  def meta = PSO
  def isValid = this.urlLink.length > 20
}
