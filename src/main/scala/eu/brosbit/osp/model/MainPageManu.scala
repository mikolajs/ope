package eu.brosbit.osp.model
/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
import _root_.net.liftweb.mongodb._
import net.liftweb.util.ConnectionIdentifier
import org.bson.types.ObjectId

case class Link(url: String, title: String)

object MainPageMenu extends MongoDocumentMeta[MainPageMenu] {
  override def collectionName = "mainpagemenu"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = MainPageMenu(ObjectId.get, "", Nil)
}

case class MainPageMenu(_id: ObjectId, var name:String, var links: List[Link])
  extends MongoDocument[MainPageMenu] {
  def meta = MainPageMenu
}






