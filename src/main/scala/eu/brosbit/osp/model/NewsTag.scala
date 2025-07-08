package eu.brosbit.osp.model

/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
import _root_.net.liftweb.mongodb._
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

object NewsTag extends MongoDocumentMeta[NewsTag] {
  override def collectionName = "newstag"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new NewsTag(ObjectId.get, "", 0)
}

case class NewsTag(var _id: ObjectId, var tag: String, var count: Int)
  extends MongoDocument[NewsTag] {
  def meta = NewsTag
}
